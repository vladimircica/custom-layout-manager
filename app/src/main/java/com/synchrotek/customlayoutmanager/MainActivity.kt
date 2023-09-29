package com.synchrotek.customlayoutmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.synchrotek.customlayoutmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        itemsAdapter = ItemsAdapter(populateItemListData() as ArrayList<Item>)
        val layoutManager = CustomGridLayoutManager(rows = 2, columns = 5)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = itemsAdapter

        // Here we should configure desired page where smooth scroller should scroll
        // Page is calculated depending of rows x matrices grid size
        binding.recyclerView.smoothScrollToPosition(38)
    }

    private fun populateItemListData(): List<Item> {
        val itemList = mutableListOf<Item>()

        for (i in 1..300) {
            itemList.add(Item("Item$i"))
        }

        return itemList
    }
}
