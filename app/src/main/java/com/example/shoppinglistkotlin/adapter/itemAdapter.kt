package com.example.shoppinglistkotlin.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.shoppinglistkotlin.R
import com.example.shoppinglistkotlin.ScrollingActivity
import com.example.shoppinglistkotlin.data.AppDatabase
import com.example.shoppinglistkotlin.data.Item
import com.example.shoppinglistkotlin.touch.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*

class itemAdapter : RecyclerView.Adapter<itemAdapter.ViewHolder>, ItemTouchHelperCallback {

    var items = mutableListOf<Item>()
    private val context: Context
    var tvTotalCost: TextView

    constructor(context: Context, listItems: List<Item>, tvTotalCost: TextView) : super() {
        this.context = context
        items.addAll(listItems)
        this.tvTotalCost = tvTotalCost
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(
            R.layout.item_row, viewGroup, false
        )
        return ViewHolder(itemRowView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items.get(position)

        viewHolder.tvQuantity.text = context.resources.getString(R.string.quantity, item.quantity.toString())
        viewHolder.tvPrice.text = context.resources.getString(
            R.string.item_price,
            context.resources.getString(R.string.format_string).format(item.price)
        )
        viewHolder.tvName.text = context.resources.getString(R.string.item_name, item.name)
        viewHolder.tvDescription.text = context.resources.getString(R.string.item_description, item.description)
        viewHolder.cbDone.isChecked = item.status

        setUpIcon(item, viewHolder)
        setOnClickListeners(viewHolder, item)
    }

    private fun setOnClickListeners(
        viewHolder: ViewHolder,
        item: Item
    ) {
        viewHolder.btnDelete.setOnClickListener {
            deleteItem(viewHolder.adapterPosition)
        }

        viewHolder.cbDone.setOnClickListener {
            item.status = viewHolder.cbDone.isChecked
            updateItem(item)
        }

        viewHolder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditItemDialog(
                item, viewHolder.adapterPosition
            )
        }
    }

    public fun setUpIcon(
        item: Item,
        viewHolder: ViewHolder
    ) {
        if (item.intCategory == 0) viewHolder.ivCategory.setImageResource(R.drawable.icon_food)
        if (item.intCategory == 1) viewHolder.ivCategory.setImageResource(R.drawable.icon_clothing)
        if (item.intCategory == 2) viewHolder.ivCategory.setImageResource(R.drawable.icon_electronics)
        if (item.intCategory == 3) viewHolder.ivCategory.setImageResource(R.drawable.icon_household)
    }

    fun updateItem(item: Item) {
        Thread {
            AppDatabase.getInstance(context).itemDao().updateItem(item)
        }.start()
    }

    fun updateItem(item: Item, editIndex: Int) {
        ScrollingActivity.totalCost -= (items[editIndex].price * items[editIndex].quantity.toFloat())
        items[editIndex] = item
        ScrollingActivity.totalCost += (item.price * item.quantity.toFloat())
        tvTotalCost.text =
            context.resources.getString(
                R.string.total_cost,
                context.resources.getString(R.string.format_string).format(ScrollingActivity.totalCost)
            )
        notifyItemChanged(editIndex)
    }

    fun addItem(item: Item) {
        items.add(0, item)
        notifyItemInserted(0)
        ScrollingActivity.totalCost += (item.price * item.quantity.toFloat())
        tvTotalCost.text =
            context.resources.getString(
                R.string.total_cost,
                context.resources.getString(R.string.format_string).format(ScrollingActivity.totalCost)
            )
    }

    fun removeAll() {
        items.clear()
        notifyDataSetChanged()
        ScrollingActivity.totalCost = 0F
        tvTotalCost.text =
            context.resources.getString(
                R.string.total_cost,
                context.resources.getString(R.string.format_string).format(ScrollingActivity.totalCost)
            )
    }

    fun deleteItem(deletePosition: Int) {
        Thread { deleteDBItem(deletePosition) }.start()
    }

    private fun deleteDBItem(deletePosition: Int) {
        AppDatabase.getInstance(context).itemDao().deleteItem(items.get(deletePosition))
        (context as ScrollingActivity).runOnUiThread {

            ScrollingActivity.totalCost -= (items.get(deletePosition).price * items.get(deletePosition).quantity.toFloat())
            tvTotalCost.text =
                context.resources.getString(
                    R.string.total_cost,
                    context.resources.getString(R.string.format_string).format(ScrollingActivity.totalCost)
                )

            items.removeAt(deletePosition)
            notifyItemRemoved(deletePosition)
        }
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvQuantity = itemView.tvItemQuantity
        var tvName = itemView.tvItemName
        var tvPrice = itemView.tvItemPrice
        var tvDescription = itemView.tvItemDescription
        var cbDone = itemView.cbStatus
        var btnDelete = itemView.btnDelete
        var btnEdit = itemView.btnEdit
        var ivCategory = itemView.ivCategory
    }
}