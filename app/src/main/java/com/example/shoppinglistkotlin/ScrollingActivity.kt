package com.example.shoppinglistkotlin

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.shoppinglistkotlin.adapter.itemAdapter
import com.example.shoppinglistkotlin.data.AppDatabase
import com.example.shoppinglistkotlin.data.Item
import com.example.shoppinglistkotlin.touch.ItemRecyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ScrollingActivity : AppCompatActivity(), ItemDialog.ItemHandler {

    lateinit var itemAdapter: itemAdapter
    var editIndex: Int = -1

    companion object {
        val KEY_ITEM_TO_EDIT = R.string.key_item_to_edit.toString()
        var totalCost = 0F
    }

    override fun itemCreated(item: Item) {
        Thread { insertItemAndRunOnUiThread(item) }.start()
    }

    private fun insertItemAndRunOnUiThread(item: Item) {
        val itemId = AppDatabase.getInstance(
            this@ScrollingActivity
        ).itemDao().insertItem(item)
        item.itemId = itemId
        runOnUiThread {
            itemAdapter.addItem(item)
        }

    }

    override fun itemUpdated(item: Item) {
        Thread { updateItemAndRunOnUiThread(item) }.start()
    }

    private fun updateItemAndRunOnUiThread(item: Item) {
        AppDatabase.getInstance(
            this@ScrollingActivity
        ).itemDao().updateItem(item)
        runOnUiThread {
            itemAdapter.updateItem(item, editIndex)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        setSupportActionBar(toolbar)
        setOnClickListeners()

        if (!wasOpenedEarlier()) {
            setUpFabPrompt()
        }
        saveFirstOpenInfo()
        initRecyclerViewFromDB()
    }

    private fun setUpFabPrompt() {
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(R.id.fab)
            .setPrimaryText(getText(R.string.add_item).toString())
            .setSecondaryText(getText(R.string.add_click_hint).toString())
            .show()
    }

    private fun setOnClickListeners() {
        var demoAnim = AnimationUtils.loadAnimation(
            this@ScrollingActivity, R.anim.demo_anim
        )

        demoAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
        })

        fab.setOnClickListener { view ->
            fab.startAnimation(demoAnim)
            showAddItemDialog()
        }

        btnDeleteAll.setOnClickListener {
            btnDeleteAll.startAnimation(demoAnim)
            Thread {
                AppDatabase.getInstance(this@ScrollingActivity).itemDao().deleteAll()
                runOnUiThread {
                    itemAdapter.removeAll()
                }
            }.start()
        }
    }

    fun saveFirstOpenInfo() {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean(getText(R.string.key_open).toString(), true)
        editor.apply()
    }

    fun wasOpenedEarlier(): Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean(getText(R.string.key_open).toString(), false)
    }

    private fun initRecyclerViewFromDB() {
        Thread { getItemsAndRunOnUiThread() }.start()
    }

    private fun getItemsAndRunOnUiThread() {
        var listItems =
            AppDatabase.getInstance(this@ScrollingActivity).itemDao().getAllItems()

        runOnUiThread {
            itemAdapter = itemAdapter(this, listItems, tvTotalCost)
            recyclerItem.layoutManager = LinearLayoutManager(this)
            recyclerItem.adapter = itemAdapter

            val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            recyclerItem.addItemDecoration(itemDecoration)

            val callback = ItemRecyclerTouchCallback(itemAdapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(recyclerItem)
        }
    }

    private fun showAddItemDialog() {
        ItemDialog().show(supportFragmentManager, getText(R.string.tag_item).toString())
    }

    public fun showEditItemDialog(itemToEdit: Item, idx: Int) {
        editIndex = idx
        val editItemDialog = ItemDialog()
        val bundle = Bundle()

        bundle.putSerializable(KEY_ITEM_TO_EDIT, itemToEdit)
        editItemDialog.arguments = bundle
        editItemDialog.show(supportFragmentManager, R.string.edit_item_dialog.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}