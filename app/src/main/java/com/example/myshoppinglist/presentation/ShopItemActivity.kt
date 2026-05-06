package com.example.myshoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.myshoppinglist.R
import com.example.myshoppinglist.data.ShopListRepositoryImpl
import com.example.myshoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputLayout

class ShopItemActivity : AppCompatActivity() {

//    private lateinit var viewModel: ShopItemViewModel
//
//    private lateinit var tilName: TextInputLayout
//    private lateinit var tilCount: TextInputLayout
//    private lateinit var etName: EditText
//    private lateinit var etCount: EditText
//    private lateinit var buttonSave: Button
//
    // Режим работы экрана: добавление или редактирование
    private var screenMode = MODE_UNKNOWN

    // ID редактируемого элемента (используется только в режиме редактирования)
    private var shopItemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shop_item_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                systemBars.left,
                maxOf(systemBars.top, imeInsets.top),
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )
            insets
        }
        parseIntent()
//        initViews()
//        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
//        addTextChangeListeners()
        launchRightMode()
//        observeViewModel()
    }

//    private fun observeViewModel() {
//        viewModel.errorInputName.observe(this) {
//            val message = if (it) {
//                getString(R.string.error_input_name)
//            } else {
//                null
//            }
//            tilName.error = message
//        }
//        viewModel.errorInputCount.observe(this) {
//            val message = if (it) {
//                getString(R.string.error_input_count)
//            } else {
//                null
//            }
//            tilCount.error = message
//        }
//        viewModel.shouldCloseScreen.observe(this) {
//            finish()
//        }
//    }
//
    private fun launchRightMode() {
        // Запускаем соответствующий режим работы экрана
        val fragment = when (screenMode) {
            MODE_ADD -> ShopItemFragment.newInstanceAddItem()
            MODE_EDIT -> ShopItemFragment.newInstanceEditItem(shopItemId)
            else -> throw RuntimeException("Unknown screen mode $screenMode")
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.shop_item_container, fragment)
            .commit()
    }
//
//    private fun addTextChangeListeners() {
//        etName.doAfterTextChanged { viewModel.resetErrorInputName() }
//        etCount.doAfterTextChanged { viewModel.resetErrorInputCount() }
//    }
//
//    private fun launchAddMode() {
//        buttonSave.setOnClickListener {
//            val name = etName.text?.toString()
//            val count = etCount.text?.toString()
//            viewModel.addShopItem(name, count)
//            Log.d(
//                "ShopItemActivity",
//                "New item Shopitem($name, $count), " +
//                        "size: ${ShopListRepositoryImpl.getShopList().value?.size}"
//            )
//        }
//    }
//
//    private fun launchEditMode() {
//        viewModel.getShopItem(shopItemId)
//        viewModel.shopItem.observe(this) {
//            etName.setText(it.name)
//            etCount.setText(it.count.toString())
//        }
//        buttonSave.setOnClickListener {
//            val name = etName.text?.toString()
//            val count = etCount.text?.toString()
//            viewModel.editShopItem(
//                name,
//                count
//            )
//            Log.d(
//                "ShopItemActivity",
//                "Edited item id: $shopItemId, " +
//                        "new value: ShopItem($name, $count), " +
//                        "size: ${ShopListRepositoryImpl.getShopList().value?.size}"
//            )
//        }
//    }
//
//    private fun initViews() {
//        tilName = findViewById<TextInputLayout>(R.id.til_name)
//        tilCount = findViewById<TextInputLayout>(R.id.til_count)
//        etName = findViewById<EditText>(R.id.et_name)
//        etCount = findViewById<EditText>(R.id.et_count)
//        buttonSave = findViewById<Button>(R.id.save_button)
//    }
//
    // Извлекает параметры из Intent и определяет режим работы Activity
    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode
        // Если режим редактирования, извлекаем ID элемента
        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
        }
    }

    companion object {

        // Ключ для передачи режима работы через Intent
        private const val EXTRA_SCREEN_MODE = "extra_mode"

        // Неизвестный режим (значение по умолчанию)
        private const val MODE_UNKNOWN = ""

        // Ключ для передачи ID элемента через Intent
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"

        // Режим редактирования существующего элемента
        private const val MODE_EDIT = "mode_edit"

        // Режим добавления нового элемента
        private const val MODE_ADD = "mode_add"

        // Создаёт Intent для запуска Activity в режиме добавления нового элемента
        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        // Создаёт Intent для запуска Activity в режиме редактирования элемента
        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }
    }
}