package com.synchrotek.customlayoutmanager

import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.RecyclerView
import java.lang.Integer.max
import java.lang.Integer.min


class CustomLayoutManager(
    private val rows: Int,
    private val columns: Int
) : RecyclerView.LayoutManager() {

    private var horizontalOffSet: Int = 0
    private val viewWidth = 300
    private val viewHeight = 95

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        fill(recycler)
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (itemCount == 0 || dx == 0) {
            return 0
        }

        val lastOffset = horizontalOffSet
        val maxOffset = calculateMaxOffset()

        // Calculate the new offset while ensuring it stays within bounds
        val newOffset = min(maxOffset, max(0, horizontalOffSet + dx))
        if (newOffset == horizontalOffSet) {
            return 0
        }
        val scrolled = newOffset - lastOffset
        horizontalOffSet = newOffset

        // Layout the items with the new offset
        fill(recycler)

        return scrolled
    }

    private fun calculateMaxOffset(): Int {
        val totalMatrixWidth = columns * viewWidth
        val itemsPerMatrix = columns * rows
        val totalMatrices = (itemCount + itemsPerMatrix - 1) / itemsPerMatrix

        // Calculate the maximum offset to ensure you cannot scroll infinitely to the right
        return max(0, totalMatrixWidth * totalMatrices - width)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        detachAndScrapAttachedViews(recycler)
        val itemsPerMatrix = rows * columns
        for (i in 0 until itemCount) {
            val matrixIndex = i / itemsPerMatrix
            val itemIndexInMatrix = i % itemsPerMatrix

            val row = itemIndexInMatrix / columns
            val col = itemIndexInMatrix % columns

            val left = (col + matrixIndex * columns) * viewWidth - horizontalOffSet
            val right = left + viewWidth
            val top = row * viewHeight
            val bottom = top + viewHeight

            val view = recycler.getViewForPosition(i)
            addView(view)

            measureChild(view, viewWidth, viewHeight)

            layoutDecorated(view, left, top, right, bottom)
        }

        val scrapListCopy = recycler.scrapList.toList()
        scrapListCopy.forEach {
            recycler.recycleView(it.itemView)
        }
    }
}
