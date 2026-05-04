import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myshoppinglist.R
import com.example.myshoppinglist.data.ShopListRepositoryImpl
import com.example.myshoppinglist.domain.ShopItem
import com.example.myshoppinglist.presentation.ShopItemActivity
import com.example.myshoppinglist.presentation.ShopItemViewModel
import com.google.android.material.textfield.TextInputLayout

class ShopItemFragment(
    private val screenMode: String = MODE_UNKNOWN,
    private val shopItemId: Int = ShopItem.UNDEFINED_ID
) : Fragment() {

    private lateinit var viewModel: ShopItemViewModel
    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etCount: EditText
    private lateinit var buttonSave: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        parseIntent()
        initViews(view)
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        addTextChangeListeners()
        launchRightMode()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.errorInputName.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_name)
            } else {
                null
            }
            tilName.error = message
        }
        viewModel.errorInputCount.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_count)
            } else {
                null
            }
            tilCount.error = message
        }
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            finish()
        }
    }

    private fun launchRightMode() {
        // Запускаем соответствующий режим работы экрана
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun addTextChangeListeners() {
        etName.doAfterTextChanged { viewModel.resetErrorInputName() }
        etCount.doAfterTextChanged { viewModel.resetErrorInputCount() }
    }

    private fun launchAddMode() {
        buttonSave.setOnClickListener {
            val name = etName.text?.toString()
            val count = etCount.text?.toString()
            viewModel.addShopItem(name, count)
            Log.d(
                "ShopItemActivity",
                "New item Shopitem($name, $count), " +
                        "size: ${ShopListRepositoryImpl.getShopList().value?.size}"
            )
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItem.observe(viewLifecycleOwner) {
            etName.setText(it.name)
            etCount.setText(it.count.toString())
        }
        buttonSave.setOnClickListener {
            val name = etName.text?.toString()
            val count = etCount.text?.toString()
            viewModel.editShopItem(
                name,
                count
            )
            Log.d(
                "ShopItemActivity",
                "Edited item id: $shopItemId, " +
                        "new value: ShopItem($name, $count), " +
                        "size: ${ShopListRepositoryImpl.getShopList().value?.size}"
            )
        }
    }

    private fun initViews(view: View) {
        tilName = view.findViewById<TextInputLayout>(R.id.til_name)
        tilCount = view.findViewById<TextInputLayout>(R.id.til_count)
        etName = view.findViewById<EditText>(R.id.et_name)
        etCount = view.findViewById<EditText>(R.id.et_count)
        buttonSave = view.findViewById<Button>(R.id.save_button)
    }

    // Извлекает параметры из Intent и определяет режим работы Activity
    private fun parseIntent() {
        if (screenMode != MODE_ADD && screenMode != MODE_EDIT) {
            throw RuntimeException("Param screen mode is absent")
        }
        if (screenMode == MODE_EDIT && shopItemId == ShopItem.UNDEFINED_ID) {
            throw RuntimeException("Param shop item id is absent")
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