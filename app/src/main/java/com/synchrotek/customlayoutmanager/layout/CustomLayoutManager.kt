package com.synchrotek.customlayoutmanager.layout

import android.content.Context
import android.graphics.PointF
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import java.lang.Integer.max
import java.lang.Integer.min


class CustomGridLayoutManager(
    private val rows: Int,
    private val columns: Int,
    private val reverseLayout: Boolean = false,
) : RecyclerView.LayoutManager(), RecyclerView.SmoothScroller.ScrollVectorProvider {

    companion object {
        const val DEFAULT_COUNT = 1
    }

    private var horizontalOffSet: Int = 0

    private var mDecoratedChildWidth: Int = 0
    private var mDecoratedChildHeight: Int = 0
    private var mVisibleColumnCount: Int = 0

    private val mTotalColumnCount: Int = DEFAULT_COUNT
    private var mVisibleRowCount: Int = 0

    private var mFirstChangedPosition: Int = 0
    private var mChangedPositionCount: Int = 0
    private var mFirstVisiblePosition: Int = 0

    // TODO Change this not to be hardcoded values
    // TODO For width and height
    private val viewWidth = 300
    private val viewHeight = 95

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        fillTemp(recycler)
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
        fillTemp(recycler)

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

//        if (childCount == 0) return
//
        var totalWidth = 0

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

            measureChildWithMargins(view, viewWidth, viewHeight)

            layoutDecoratedWithMargins(view, left, top, right, bottom)
        }

        val scrapListCopy = recycler.scrapList.toList()
        scrapListCopy.forEach {
            recycler.recycleView(it.itemView)
        }

        // Iterate through child views and sum up their widths
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val params = child?.layoutParams as RecyclerView.LayoutParams
            totalWidth += getDecoratedMeasuredWidth(child) + params.leftMargin + params.rightMargin
        }

        println("Sum of totalWidth -> $totalWidth")
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        detachAndScrapAttachedViews(recycler)
        val itemsPerMatrix = rows * columns
        val matrices = itemCount / itemsPerMatrix

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

            layoutDecorated(view, left, top, right, bottom)
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

    @Override
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView, state: RecyclerView.State?,
        position: Int
    ) {
        val itemsPerPage = rows * columns
        val currentPage = position / itemsPerPage
        val nextPageStart = currentPage * itemsPerPage

        val smoothScroller = CustomSmoothScroller(recyclerView.context)

        if (reverseLayout) {
            smoothScroller.targetPosition = nextPageStart + (rows * columns) - 1
        } else {
            smoothScroller.targetPosition = nextPageStart
        }
        startSmoothScroll(smoothScroller)
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        val direction = if (reverseLayout) -1f else 1f
        return PointF(direction, 0f)
    }

    private inner class CustomSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return this@CustomGridLayoutManager.computeScrollVectorForPosition(targetPosition)
        }

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }

    private fun updateWindowSizing() {
        mVisibleColumnCount = getHorizontalSpace() / mDecoratedChildWidth + 1

        if (getHorizontalSpace() % mDecoratedChildWidth > 0) {
            mVisibleColumnCount++
        }


        if (mVisibleColumnCount > getTotalColumnCount()) {
            mVisibleColumnCount = getTotalColumnCount()
        }

        mVisibleRowCount = getVerticalSpace() / mDecoratedChildHeight + 1

        if (getVerticalSpace() % mDecoratedChildHeight > 0) {
            mVisibleRowCount++
        }

        if (mVisibleRowCount > getTotalRowCount()) {
            mVisibleRowCount = getTotalRowCount()
        }
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingRight - paddingLeft
    }

    private fun getTotalColumnCount(): Int {
        return if (itemCount < mTotalColumnCount) {
            itemCount
        } else mTotalColumnCount
    }

    private fun getVerticalSpace(): Int {
        return height - paddingBottom - paddingTop
    }

    private fun getTotalRowCount(): Int {
        if (itemCount == 0 || mTotalColumnCount == 0) {
            return 0
        }
        var maxRow = itemCount / mTotalColumnCount
        if (itemCount % mTotalColumnCount != 0) {
            maxRow++
        }
        return maxRow
    }

    /**\
     * Try to implement onLayoutChildren using getDecoratedMeasuredWidth and
     * getDecoratedMeasuredHeight. Also using getDecoratedLeft and getDecoratedRight
     * to overcome issues with layout measure in case RTL is enabled
     */
    private fun fillGridNew(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            return
        }

        if (childCount == 0 && state.isPreLayout) {
            return
        }

        if (!state.isPreLayout) {
            mFirstChangedPosition = 0
            mChangedPositionCount = 0
        }

        if (childCount == 0) { //First or empty layout
            //Scrap measure one child
            val scrap = recycler.getViewForPosition(0)
            addView(scrap)
            measureChildWithMargins(scrap, 0, 0)

            mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap)
            mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap)
            detachAndScrapView(scrap, recycler)
        }


        //Always update the visible row/column counts
        updateWindowSizing()

        var childLeft: Int
        var childTop: Int

        if (childCount == 0) {
            mFirstVisiblePosition = 0
            childLeft = paddingLeft
            childTop = paddingTop
        } else if (!state.isPreLayout
            && getVisibleChildCount() >= state.itemCount
        ) {
            mFirstVisiblePosition = 0
            childLeft = paddingLeft
            childTop = paddingTop
        } else {
            val topChild = getChildAt(0)
            childLeft = getDecoratedLeft(topChild!!)
            childTop = getDecoratedTop(topChild)
        }

        if (!state.isPreLayout && getVerticalSpace() > getTotalRowCount() * mDecoratedChildHeight) {
            mFirstVisiblePosition %= getTotalColumnCount()
            childTop = paddingTop

            if (mFirstVisiblePosition + mVisibleColumnCount > state.itemCount) {
                mFirstVisiblePosition = (state.itemCount - mVisibleColumnCount).coerceAtLeast(0)
                childLeft = paddingLeft
            }


            val maxFirstRow = getTotalRowCount() - (mVisibleRowCount - 1)
            val maxFirstCol = getTotalColumnCount() - (mVisibleColumnCount - 1)
            val isOutOfRowBounds: Boolean = getFirstVisibleRow() > maxFirstRow
            val isOutOfColBounds: Boolean = getFirstVisibleColumn() > maxFirstCol

            if (isOutOfRowBounds || isOutOfColBounds) {
                val firstRow: Int = if (isOutOfRowBounds) {
                    maxFirstRow
                } else {
                    getFirstVisibleRow()
                }

                val firstCol: Int = if (isOutOfColBounds) {
                    maxFirstCol
                } else {
                    getFirstVisibleColumn()
                }

                mFirstVisiblePosition = firstRow * getTotalColumnCount() + firstCol
                childLeft = getHorizontalSpace() - mDecoratedChildWidth * mVisibleColumnCount
                childTop = getVerticalSpace() - mDecoratedChildHeight * mVisibleRowCount

                if (getFirstVisibleRow() == 0) {
                    childTop = childTop.coerceAtMost(paddingTop)
                }
                if (getFirstVisibleColumn() == 0) {
                    childLeft = childLeft.coerceAtMost(paddingLeft)
                }
            }
        }

        detachAndScrapAttachedViews(recycler)


        //Evaluate any disappearing views that may exist
        if (!state.isPreLayout && !recycler.scrapList.isEmpty()) {
            val scrapList = recycler.scrapList
            val disappearingViews = HashSet<View>(scrapList.size)
            for (holder in scrapList) {

            }
            for (child in disappearingViews) {
                layoutDisappearingView(child)
            }
        }
    }

    private fun layoutDisappearingView(disappearingChild: View) {

    }

    private fun getGlobalRowOfPosition(position: Int): Int {
        return position / mTotalColumnCount
    }

    private fun getGlobalColumnOfPosition(position: Int): Int {
        return position % mTotalColumnCount
    }

    private fun getFirstVisibleRow(): Int {
        return mFirstVisiblePosition / getTotalColumnCount()
    }

    private fun getFirstVisibleColumn(): Int {
        return mFirstVisiblePosition % getTotalColumnCount()
    }

    //TODO We should implement Utils class for this
    private fun getVisibleChildCount(): Int {
        return mVisibleColumnCount * mVisibleRowCount
    }
}
