package com.example.przeterminarz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        productList = ArrayList()
        productAdapter = ProductAdapter(productList)
        productDAO = ProductDAO(requireContext())
        initProductListItems()

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = productAdapter

        updateListSizeTextView(productList.size)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonFilterCategory1.setOnClickListener {
            productAdapter.filterByCategory(Categories.CATEGORY1)
            updateListSizeTextView(productAdapter.itemCount)
            //TODO test only!
            var products = productDAO.getAllProducts();
            println(products);
        }

        binding.buttonFilterCategory2.setOnClickListener {
            productAdapter.filterByCategory(Categories.CATEGORY2)
            updateListSizeTextView(productAdapter.itemCount)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initProductListItems(){
        productList.add(Product("Ball 1", "drawable/ball.jpg", Categories.CATEGORY1))
        productList.add(Product("Ball 2", "drawable/ball2.jpg", Categories.CATEGORY2))
    }

    private fun updateListSizeTextView(size: Int) {
        binding.textView.text = buildString {
            append(getString(R.string.list_size))
            append(size)
        }
    }
}