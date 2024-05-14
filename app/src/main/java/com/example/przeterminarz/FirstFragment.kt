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
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var productList : ArrayList<Product>
    private lateinit var productAdapter : ProductAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        productList = ArrayList()
        productAdapter = ProductAdapter(productList)
        initProductListItems()
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this.context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = productAdapter

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonFilterCategory1.setOnClickListener {
            productAdapter.filterByCategory(Categories.CATEGORY1)
        }

        binding.buttonFilterCategory2.setOnClickListener {
            productAdapter.filterByCategory(Categories.CATEGORY2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initProductListItems(){
        productList.add(Product("Ball 1", R.drawable.ball, Categories.CATEGORY1))
        productList.add(Product("Ball 2", R.drawable.ball2, Categories.CATEGORY2))
    }
}