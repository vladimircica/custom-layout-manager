package com.synchrotek.customlayoutmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(private val itemList: ArrayList<Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_SPECIAL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val viewLayout = inflater.inflate(R.layout.item_rv, parent, false)
                ItemViewHolder(viewLayout)
            }

//            VIEW_TYPE_SPECIAL -> {
//                val viewLayout = inflater.inflate(R.layout.item_rv, parent, false)
//                SpecialItemViewHolder(viewLayout)
//            }
            // Define more cases for different view types as needed
            else -> throw IllegalArgumentException("Invalid viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = itemList[position]

        when (holder) {
            is ItemViewHolder -> {
                // Bind data for normal items
                holder.itemName.text = currentItem.itemName
            }

//            is SpecialItemViewHolder -> {
//                // Bind data for special items
//                holder.itemName.text = "|"
//            }
            // Handle more view types if necessary
        }
    }

    override fun getItemCount() = itemList.size

//    override fun getItemViewType(position: Int): Int {
//        return when (position) {
//            5 -> VIEW_TYPE_SPECIAL
//            else -> VIEW_TYPE_NORMAL
//        }
//    }
}
