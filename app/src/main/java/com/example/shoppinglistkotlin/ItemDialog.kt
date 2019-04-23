package com.example.shoppinglistkotlin

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import com.example.shoppinglistkotlin.data.Item
import kotlinx.android.synthetic.main.new_item_dialog.view.*
import java.lang.RuntimeException

class ItemDialog : DialogFragment(), AdapterView.OnItemSelectedListener {
    interface ItemHandler {
        fun itemCreated(item: Item)
        fun itemUpdated(item: Item)
    }

    private lateinit var itemHandler: ItemHandler
    private lateinit var spinnerItemCategory: Spinner
    private lateinit var etItemName: EditText
    private lateinit var etItemPrice: EditText
    private lateinit var etItemDescription: EditText
    private lateinit var etItemQuantity: EditText

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is ItemHandler) itemHandler = context
        else throw RuntimeException(R.string.item_handler_error.toString())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val rootView = initializeRootView()
        val listItems = resources.getStringArray(R.array.item_categories)

        builder.setTitle(R.string.new_item)
        builder.setView(rootView)
        setUpEditTextFields(builder)
        builder.setPositiveButton(R.string.ok) { dialog, witch -> }

        setUpCategoryAdapter(listItems)
        return builder.create()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener { createOrEditAfterCheckingFields() }
    }

    private fun setUpEditTextFields(builder: AlertDialog.Builder) {
        val arguments = this.arguments
        if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
            val shoppingListItem = arguments.getSerializable(ScrollingActivity.KEY_ITEM_TO_EDIT) as Item
            setTextForItemEdit(shoppingListItem, builder)
        }
    }

    private fun setTextForItemEdit(
        shoppingListItem: Item,
        builder: AlertDialog.Builder
    ) {
        etItemName.setText(shoppingListItem.name)
        etItemPrice.setText(shoppingListItem.price.toString())
        etItemDescription.setText(shoppingListItem.description)
        etItemQuantity.setText(shoppingListItem.quantity.toString())
        builder.setTitle(R.string.edit_item)
    }

    private fun setUpCategoryAdapter(listItems: Array<String>) {
        val categoryAdapter = ArrayAdapter(this.context!!, android.R.layout.simple_list_item_1, listItems)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerItemCategory.adapter = categoryAdapter
        spinnerItemCategory.onItemSelectedListener = this
    }

    private fun initializeRootView(): View? {
        val rootView = requireActivity().layoutInflater.inflate(R.layout.new_item_dialog, null)
        spinnerItemCategory = rootView.spinnerItemCategory
        etItemName = rootView.etItemName
        etItemPrice = rootView.etItemPrice
        etItemDescription = rootView.etItemDescription
        etItemQuantity = rootView.etItemQuantity
        return rootView
    }

    private fun createOrEditAfterCheckingFields() {
        val formOk = checkAllFieldsValid()
        if (formOk) editOrCreateItem()
    }

    private fun checkAllFieldsValid(): Boolean {
        var formOk = true

        formOk = checkForEmptyTextFields(formOk)
        if (etItemQuantity.text.toString().toInt() <= 0) {
            etItemQuantity.error =
                getText(R.string.small_quantity).toString()
            formOk = false
        }
        if (etItemPrice.text.toString().toFloat() <= 0F) {
            etItemPrice.error = getText(R.string.small_price).toString()
            formOk = false
        }

        return formOk
    }

    private fun checkForEmptyTextFields(formOk: Boolean): Boolean {
        var formOk1 = formOk
        if (etItemQuantity.text.isEmpty()) {
            etItemQuantity.error = getText(R.string.empty_quantity).toString()
            formOk1 = false
        }
        if (etItemName.text.isEmpty()) {
            etItemName.error = getText(R.string.empty_name).toString()
            formOk1 = false
        }
        if (etItemPrice.text.isEmpty()) {
            etItemPrice.error = getText(R.string.empty_price).toString()
            formOk1 = false
        }
        if (etItemDescription.text.isEmpty()) {
            etItemDescription.error = getText(R.string.empty_description).toString()
            formOk1 = false
        }
        return formOk1
    }

    private fun editOrCreateItem() {
        if (etItemName.text.isNotEmpty() && etItemPrice.text.isNotEmpty() && etItemDescription.text.isNotEmpty() && etItemQuantity.text.isNotEmpty()) {
            val arguments = this.arguments
            if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
                handleItemEdit()
            } else {
                handleItemCreate()
            }
            dialog.dismiss()
        }
    }

    private fun handleItemCreate() {
        itemHandler.itemCreated(createItem())
    }

    private fun createItem(): Item {
        return Item(
            null,
            getSpinnerCategory(spinnerItemCategory.selectedItemPosition),
            etItemName.text.toString(),
            etItemPrice.text.toString().toFloat(),
            etItemDescription.text.toString(),
            spinnerItemCategory.selectedItemPosition,
            false,
            etItemQuantity.text.toString().toInt()
        )
    }

    private fun getSpinnerCategory(intCategory: Int): String {
        if (intCategory == 0) return resources.getString(R.string.category_food)
        if (intCategory == 1) return resources.getString(R.string.category_clothing)
        if (intCategory == 2) return resources.getString(R.string.category_electronics)
        return resources.getString(R.string.category_household)
    }

    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(ScrollingActivity.KEY_ITEM_TO_EDIT) as Item

        var oldTotalCost = ScrollingActivity.totalCost
        var oldItemCost = itemToEdit.price * itemToEdit.quantity.toFloat()

        editItem(itemToEdit)

        ScrollingActivity.totalCost = (oldTotalCost - oldItemCost) + (itemToEdit.quantity.toFloat() * itemToEdit.price)
        itemHandler.itemUpdated(itemToEdit)
    }

    private fun editItem(itemToEdit: Item) {
        itemToEdit.category = getSpinnerCategory(spinnerItemCategory.selectedItemPosition)
        itemToEdit.name = etItemName.text.toString()
        itemToEdit.price = etItemPrice.text.toString().toFloat()
        itemToEdit.description = etItemDescription.text.toString()
        itemToEdit.intCategory = spinnerItemCategory.selectedItemPosition
        itemToEdit.quantity = etItemQuantity.text.toString().toInt()
    }
}