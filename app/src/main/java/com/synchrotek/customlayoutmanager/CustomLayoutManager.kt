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
        val lastOffset = horizontalOffSet
        val maxOffset = calculateMaxOffset()

        // Calculate the new offset while ensuring it stays within bounds
        horizontalOffSet = min(maxOffset, max(0, horizontalOffSet + dx))

        // If the offset didn't change, we didn't scroll
        if (horizontalOffSet == lastOffset) {
            return 0
        }

        // Layout the items with the new offset
        fill(recycler)

        // Return the actual amount scrolled
        return horizontalOffSet - lastOffset
    }

    private fun calculateMaxOffset(): Int {
        val itemCount = rows
        if (itemCount == 0) return 0

        val lastItemPosition = itemCount - 1
        val lastItemRight = lastItemPosition * viewWidth

        return max(0, lastItemRight - width)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        detachAndScrapAttachedViews(recycler)
        for (i in 0 until rows) {
            val left = i * viewWidth - horizontalOffSet
            val right = left + viewWidth
            val top = 0
            val bottom = top + viewWidth

            val view = recycler.getViewForPosition(i)
            addView(view)

            measureChild(view, viewWidth, viewWidth)

            layoutDecorated(view, left, top, right, bottom)
        }

        val scrapListCopy = recycler.scrapList.toList()
        scrapListCopy.forEach {
            recycler.recycleView(it.itemView)
        }
    }
}
