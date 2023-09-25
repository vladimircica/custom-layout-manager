package com.synchrotek.customlayoutmanager

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val itemName: TextView = itemView.findViewById(R.id.item_name)
}
