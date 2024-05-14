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
class FirstFragment() : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var productList : ArrayList<Product>
    private lateinit var productAdapter : ProductAdapter
    private lateinit var productDAO: ProductDAO
    private var selectedCategories = HashSet<Categories>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        productDAO = ProductDAO(requireContext())
        productList = initProductListItems()
        productAdapter = ProductAdapter(productList, productDAO) { product ->
            val bundle = Bundle().apply {
                putParcelable("product", product)
                putBoolean("edit_mode", true)
            }
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = productAdapter

        updateListSizeTextView(productAdapter.itemCount)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.spinnerCategory.setOnClickListener {
            showCategoryMultiSelectDialog()
            updateListSizeTextView(productAdapter.itemCount);
            println("SIZE: " + productAdapter.itemCount);
            println("SIZE2: " + productList.size);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initProductListItems(): ArrayList<Product> {
        return productDAO.getAllProducts() as ArrayList<Product>;
    }

    private fun updateListSizeTextView(size: Int) {
        binding.textView.text = buildString {
            append(getString(R.string.list_size))
            append(size)
        }
    }

    private fun showCategoryMultiSelectDialog() {
        val categories = Categories.entries
        val checkedItems = BooleanArray(categories.size) { selectedCategories.contains(categories[it]) }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_categories)

        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_multiselect_categories, null)
        val checkboxContainer = dialogView.findViewById<LinearLayout>(R.id.checkbox_container)

        for (i in categories.indices) {
            val category = categories[i]
            val checkBox = CheckBox(requireContext()).apply {
                text = category.name
                isChecked = checkedItems[i]
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCategories.add(category)
                    } else {
                        selectedCategories.remove(category)
                    }
                }
            }
            checkboxContainer.addView(checkBox)
        }

        builder.setView(dialogView)
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            // Just return size of filtered list and updateListSizeTextView at once
            updateListSizeTextView(productAdapter.filterByCategories(selectedCategories))
            dialog.dismiss()
        }
        builder.create().show()
    }
}