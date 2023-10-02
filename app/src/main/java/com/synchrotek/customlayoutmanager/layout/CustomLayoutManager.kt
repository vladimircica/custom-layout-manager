package com.synchrotek.customlayoutmanager.layout

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
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
    private val context: Context
) : RecyclerView.LayoutManager(), RecyclerView.SmoothScroller.ScrollVectorProvider {

    companion object {
        const val DEFAULT_COUNT = 1
        const val viewWidth = 300
        const val viewHeight = 95
    }

    private var horizontalOffSet: Int = 0

    private var mDecoratedChildWidth: Int = 0
    private var mDecoratedChildHeight: Int = 0
    private var mVisibleColumnCount: Int = 0

    private val mTotalColumnCount: Int = DEFAULT_COUNT
    private var mVisibleRowCount: Int = 0
    private var mFirstVisiblePosition: Int = 0


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        fill(recycler, state)
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
        fill(recycler, state)

        return scrolled
    }

    private fun calculateMaxOffset(): Int {
        val totalMatrixWidth = columns * viewWidth
        val itemsPerMatrix = columns * rows
        val totalMatrices = (itemCount + itemsPerMatrix - 1) / itemsPerMatrix

        // Calculate the maximum offset to ensure you cannot scroll infinitely to the right or left
        return max(0, totalMatrixWidth * totalMatrices - width)
    }

    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            return
        }

        if (childCount == 0 && state.isPreLayout) {
            return
        }

        detachAndScrapAttachedViews(recycler)

        val itemsPerMatrix = rows * columns
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val displayWidthInPixels = displayMetrics.widthPixels

        for (i in 0 until itemCount) {
            val matrixIndex = i / itemsPerMatrix
            val itemIndexInMatrix = i % itemsPerMatrix

            val row = itemIndexInMatrix / columns
            val col = itemIndexInMatrix % columns

            var right: Int
            var left: Int
            if (reverseLayout) {
                right =
                    displayWidthInPixels - ((col + matrixIndex * columns) * viewWidth - horizontalOffSet)
                left = right - viewWidth
            } else {
                left = (col + matrixIndex * columns) * viewWidth - horizontalOffSet
                right = left + viewWidth
            }

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
    }

    /**
     * This method is intended to be used in coloration with the computescrollVectorFotPosition and
     * and passing desired page number. It should replace smoothToScrollPosition method call
     */
    fun setPageNumber(pageNumber: Int) {
        computeScrollVectorForPosition(targetPosition = pageNumber)
    }

    @Override
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView, state: RecyclerView.State?,
        position: Int
    ) {
        val itemsPerPage = rows * columns
        val nextPageStart = position * itemsPerPage

        val smoothScroller = CustomSmoothScroller(recyclerView.context)
        smoothScroller.targetPosition = nextPageStart

        startSmoothScroll(smoothScroller)
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF {
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

    /**
     * Try to implement onLayoutChildren using getDecoratedMeasuredWidth and
     * getDecoratedMeasuredHeight. Also using getDecoratedLeft and getDecoratedRight
     * to overcome issues with layout measure in case RTL is enabled and to support
     * item decoration logic. Or when dimensions of matrices are greater then display size
     * All methods from this point below are related to new fill grid approach
     */
    private fun fillGridNew(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            return
        }

        if (childCount == 0 && state.isPreLayout) {
            return
        }

        if (childCount == 0) {
            val scrap = recycler.getViewForPosition(0)
            addView(scrap)
            measureChildWithMargins(scrap, 0, 0)

            mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap)
            mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap)
            detachAndScrapView(scrap, recycler)
        }

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


        if (!state.isPreLayout && recycler.scrapList.isNotEmpty()) {
            val scrapList = recycler.scrapList
            val disappearingViews = HashSet<View>(scrapList.size)

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

    private fun getVisibleChildCount(): Int {
        return mVisibleColumnCount * mVisibleRowCount
    }
}
