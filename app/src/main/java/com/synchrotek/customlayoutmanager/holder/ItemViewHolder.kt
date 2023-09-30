package com.synchrotek.customlayoutmanager.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synchrotek.customlayoutmanager.R

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val itemName: TextView = itemView.findViewById(R.id.item_name)
}
