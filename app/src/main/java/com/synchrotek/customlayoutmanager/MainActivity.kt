package com.synchrotek.customlayoutmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synchrotek.customlayoutmanager.databinding.ActivityMainBinding
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

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
        val layoutManager = CustomGridLayoutManager(rows = 2, columns = 5)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = itemsAdapter

        binding.recyclerView.smoothScrollToPosition(25)
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
        itemList.add(Item("Item19"))
        itemList.add(Item("Item20"))
        itemList.add(Item("Item21"))
        itemList.add(Item("Item22"))
        itemList.add(Item("Item23"))
        itemList.add(Item("Item24"))
        itemList.add(Item("Item25"))
        itemList.add(Item("Item26"))
        itemList.add(Item("Item27"))
        itemList.add(Item("Item28"))
        itemList.add(Item("Item29"))
        itemList.add(Item("Item30"))
        itemList.add(Item("Item31"))
        itemList.add(Item("Item32"))
        itemList.add(Item("Item33"))
    }
}
