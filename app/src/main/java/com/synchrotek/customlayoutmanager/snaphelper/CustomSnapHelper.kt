package com.synchrotek.customlayoutmanager.snaphelper

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class CustomSnapHelper(
    private val rows: Int,
    private val columns: Int,
) : LinearSnapHelper() {

    private var mPosition = 10

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
