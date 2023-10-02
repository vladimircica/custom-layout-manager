package com.synchrotek.customlayoutmanager.snaphelper

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class CustomSnapHelper(
    private val rows: Int,
    private val columns: Int,
    private val reverseLayout: Boolean = false,
) : LinearSnapHelper() {

    private var mPosition = rows * columns

    /**
     * Should always scroll whole page. Meaning 10 position gaps which
     * represents one whole page if matrices are 2 x 5 dimensions
     */
    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager?,
        velocityX: Int,
        velocityY: Int
    ): Int {
        val itemsPerPage = rows * columns
        val currentPage = mPosition / itemsPerPage
        val nextPageStart = currentPage * itemsPerPage

        mPosition += itemsPerPage

        return nextPageStart + 2
    }
}
