package com.synchrotek.customlayoutmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.synchrotek.customlayoutmanager.adapters.ItemsAdapter
import com.synchrotek.customlayoutmanager.databinding.ActivityMainBinding
import com.synchrotek.customlayoutmanager.layout.CustomGridLayoutManager
import com.synchrotek.customlayoutmanager.model.Item

class MainActivity : AppCompatActivity() {

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        itemsAdapter = ItemsAdapter(populateItemListData() as ArrayList<Item>)
        val layoutManager = CustomGridLayoutManager(rows = 2, columns = 5, false)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = itemsAdapter

        // Here we should configure desired item where smooth scroller should scroll
        // Page is calculated depending of rows x matrices grid size
        // If item is contained it that page calculation fill align that page
        // On left and right edges
        // For example item 38 should be located in page 3 since there are 10 items per page
        binding.recyclerView.smoothScrollToPosition(38)

        // Add simple ItemDecorator to RecyclerView which overrides default insets
        // When calculation of insets item decorator is finished we can add custom Inset Decorator to RV
    }

    private fun populateItemListData(): List<Item> {
        val itemList = mutableListOf<Item>()

        for (i in 1..300) {
            itemList.add(Item("Item$i"))
        }

        return itemList
    }
}
