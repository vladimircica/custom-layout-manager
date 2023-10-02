package com.synchrotek.customlayoutmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.synchrotek.customlayoutmanager.adapters.ItemsAdapter
import com.synchrotek.customlayoutmanager.databinding.ActivityMainBinding
import com.synchrotek.customlayoutmanager.layout.CustomGridLayoutManager
import com.synchrotek.customlayoutmanager.model.Item
import com.synchrotek.customlayoutmanager.snaphelper.CustomSnapHelper

class MainActivity : AppCompatActivity() {

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val rows = 2
        val columns = 5

        itemsAdapter = ItemsAdapter(populateItemListData() as ArrayList<Item>)
        val layoutManager =
            CustomGridLayoutManager(
                rows = rows,
                columns = columns,
                context = this
            )

        val recyclerView = binding.recyclerView

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = itemsAdapter

        // We should configure desired page where smooth scroller should scroll
        // Page is calculated depending of rows x matrices grid size
        // For example page 8 should scroll to items between 81 and 90
        recyclerView.smoothScrollToPosition(8)

        // Add simple ItemDecorator to RecyclerView which overrides default insets
        // When calculation of insets item decorator is finished we can add custom Inset Decorator to RV
        // In such way as recycler.addItemDecoration(InsetDecoration(this))

        val snapHelper = CustomSnapHelper(rows, columns)
        snapHelper.attachToRecyclerView(recyclerView)
    }

    private fun populateItemListData(): List<Item> {
        val itemList = mutableListOf<Item>()

        for (i in 1..300) {
            itemList.add(Item("Item$i"))
        }

        return itemList
    }
}
