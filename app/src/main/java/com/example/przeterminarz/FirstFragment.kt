package com.example.przeterminarz

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.przeterminarz.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var productList: ArrayList<Product>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productDAO: ProductDAO
    private var selectedCategories = HashSet<Categories>()
    private var selectedStates = HashSet<States>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        productDAO = ProductDAO(requireContext())
        productList = initProductListItems()
        productAdapter = ProductAdapter(productList, productDAO, { product -> //normal click listener
            if (isProductEditable(product.expirationDate))
            {
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                    putBoolean("edit_mode", true)
                }
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
            }
            else
            {
                showExpiredProductDialog()
            }
        }, { product -> //long click listener
            if (isProductEditable(product.expirationDate))
            {
                showDeleteProductDialog(product)
            }
            else
            {
                showMarkProductAsDiscardedDialog(product)
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = productAdapter

        updateListSizeTextView(productAdapter.itemCount)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonCategory.setOnClickListener {
            showCategoryMultiSelectDialog()
            updateListSizeTextView(productAdapter.itemCount)
        }

        binding.buttonState.setOnClickListener {
            showStateMultiSelectDialog()
            updateListSizeTextView(productAdapter.itemCount)
        }
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    private fun initProductListItems(): ArrayList<Product>
    {
        return productDAO.getAllProducts() as ArrayList<Product>
    }

    private fun updateListSizeTextView(size: Int)
    {
        binding.textView.text = buildString {
            append(getString(R.string.list_size))
            append(size)
        }
    }

    private fun showDeleteProductDialog(product: Product)
    {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.delete_product)
        builder.setMessage(R.string.are_you_sure_delete)
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            productDAO.deleteProduct(product)
            productList.remove(product)
            productAdapter.notifyDataSetChanged()
            updateListSizeTextView(productAdapter.itemCount)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showCategoryMultiSelectDialog()
    {
        val categories = Categories.entries
        val checkedItems = BooleanArray(categories.size) { selectedCategories.contains(categories[it]) }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_categories)

        val inflater = requireActivity().layoutInflater
        val categoriesDialogView = inflater.inflate(R.layout.dialog_multiselect_categories, null)
        val categoriesCheckboxContainer = categoriesDialogView.findViewById<LinearLayout>(R.id.categories_checkbox_container)

        for (i in categories.indices)
        {
            val category = categories[i]
            val checkBox = CheckBox(requireContext()).apply {
                text = category.name
                isChecked = checkedItems[i]
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked)
                    {
                        selectedCategories.add(category)
                    }
                    else
                    {
                        selectedCategories.remove(category)
                    }
                }
            }
            categoriesCheckboxContainer.addView(checkBox)
        }
        builder.setView(categoriesDialogView)
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            updateListSizeTextView(productAdapter.filterByCategoriesAndStates(selectedCategories, selectedStates))
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showStateMultiSelectDialog()
    {
        val states = States.entries
        val checkedItems = BooleanArray(states.size) { selectedStates.contains(states[it]) }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_states)

        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_multiselect_states, null)
        val checkboxContainer = dialogView.findViewById<LinearLayout>(R.id.states_checkbox_container)

        for (i in states.indices)
        {
            val state = states[i]
            val checkBox = CheckBox(requireContext()).apply {
                text = state.name
                isChecked = checkedItems[i]
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedStates.add(state)
                    } else {
                        selectedStates.remove(state)
                    }
                }
            }
            checkboxContainer.addView(checkBox)
        }
        builder.setView(dialogView)
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            updateListSizeTextView(productAdapter.filterByCategoriesAndStates(selectedCategories, selectedStates))
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun isProductEditable(expirationDate: Long): Boolean
    {
        val currentDate = System.currentTimeMillis()
        //just simply convert timestamp to days and check if expirationDay is at least for next day
        val expirationDateOnly = expirationDate / (1000 * 60 * 60 * 24)
        val currentDateOnly = currentDate / (1000 * 60 * 60 * 24)
        return expirationDateOnly > currentDateOnly
    }

    private fun showExpiredProductDialog()
    {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.expired_product_title))
        builder.setMessage(getString(R.string.expired_product_message))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showMarkProductAsDiscardedDialog(product: Product)
    {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.expired_product_title)
        builder.setMessage(R.string.are_you_sure_mark_as_expired)
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            product.isDiscarded = true;
            productDAO.updateProduct(product)
            productAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

}