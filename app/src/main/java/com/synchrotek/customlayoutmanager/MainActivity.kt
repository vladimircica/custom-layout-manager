package com.synchrotek.customlayoutmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.synchrotek.customlayoutmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var itemList: ArrayList<Item>
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        itemList = ArrayList()
        itemListItems()
        itemsAdapter = ItemsAdapter(itemList)
        binding.recyclerView.layoutManager = CustomLayoutManager(2, 5)
        binding.recyclerView.adapter = itemsAdapter
    }

    private fun itemListItems() {
        itemList.add(Item("Item1"))
        itemList.add(Item("Item2"))
        itemList.add(Item("Item3"))
        itemList.add(Item("Item4"))
        itemList.add(Item("Item5"))
        itemList.add(Item("Item6"))
        itemList.add(Item("Item7"))
        itemList.add(Item("Item8"))
        itemList.add(Item("Item9"))
        itemList.add(Item("Item10"))
        itemList.add(Item("Item11"))
        itemList.add(Item("Item12"))
        itemList.add(Item("Item13"))
        itemList.add(Item("Item14"))
        itemList.add(Item("Item15"))
        itemList.add(Item("Item16"))
        itemList.add(Item("Item17"))
        itemList.add(Item("Item18"))
    }
}
