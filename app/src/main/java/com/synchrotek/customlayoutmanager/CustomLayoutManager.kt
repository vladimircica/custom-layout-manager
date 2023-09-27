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

    //TODO This should be added to constructor to be able to configure manually
    //TODO Use this value for reverse layout
    private var reverseLayout: Boolean = false

    // TODO Change this not to be hardcoded values
    // TODO For width and height
    private val viewWidth = 300
    private val viewHeight = 95

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        println("onLayoutChildren")
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

    //TODO This method should be refactored and used for both LTR and RTL
    //TODO For now use just fillTemp method. Will merge it with fill
    private fun fillTemp(recycler: RecyclerView.Recycler) {
        detachAndScrapAttachedViews(recycler)
        val itemsPerMatrix = rows * columns
        val matrices = itemCount / itemsPerMatrix
        val totalContentWidth = columns * itemsPerMatrix

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

            layoutDecoratedWithMargins(view, left, top, right, bottom)
        }

        val scrapListCopy = recycler.scrapList.toList()
        scrapListCopy.forEach {
            recycler.recycleView(it.itemView)
        }
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        detachAndScrapAttachedViews(recycler)
        val itemsPerMatrix = rows * columns
        val matrices = itemCount / itemsPerMatrix
        println("matrices number -> $matrices")

        val totalContentWidth = columns * itemsPerMatrix

        for (i in 0 until itemCount) {
            val matrixIndex = (i / itemsPerMatrix)
            val itemIndexInMatrix = (itemsPerMatrix - 1) - (i % itemsPerMatrix)
            val col = itemIndexInMatrix % columns
            val row = (itemsPerMatrix / columns - 1) - (itemIndexInMatrix / columns)

            val left =
                totalContentWidth - (((columns - 1 - col) + matrixIndex * columns) * viewWidth - horizontalOffSet)
            val right = left + viewWidth
            val top = row * viewHeight
            val bottom = top + viewHeight


            val view = recycler.getViewForPosition(i)
            addView(view)

            measureChild(view, viewWidth, viewHeight)

            layoutDecoratedWithMargins(view, left, top, right, bottom)
        }

        val scrapListCopy = recycler.scrapList.toList()
        scrapListCopy.forEach {
            recycler.recycleView(it.itemView)
        }
    }

    private fun calculateTotalContentWidth(): Int {
        // Calculate the total width of your content here
        // You may need to iterate through your items and matrices
        // and sum up the widths of all elements
        var totalWidth = 0
        for (i in 0 until itemCount) {
            val childView = getChildAt(i)
            if (childView != null) {
                val childWidth = getRightDecorationWidth(childView)
                totalWidth += childWidth
            }
        }

        return totalWidth
    }
}
