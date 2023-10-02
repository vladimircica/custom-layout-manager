package com.synchrotek.customlayoutmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.synchrotek.customlayoutmanager.model.Item
import com.synchrotek.customlayoutmanager.holder.ItemViewHolder
import com.synchrotek.customlayoutmanager.R

class ItemsAdapter(private val itemList: ArrayList<Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_NORMAL = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val viewLayout = inflater.inflate(R.layout.item_rv, parent, false)
                ItemViewHolder(viewLayout)
            }

            else -> throw IllegalArgumentException("Invalid viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = itemList[position]
        when (holder) {
            is ItemViewHolder -> {
                holder.itemName.text = currentItem.itemName
            }
        }
    }

    override fun getItemCount() = itemList.size
}
