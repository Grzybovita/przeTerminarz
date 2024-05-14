package com.example.przeterminarz

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.przeterminarz.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private lateinit var productDAO: ProductDAO
    private var selectedImageUri: Uri? = null

    private val getImageContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                loadImage(uri.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        productDAO = ProductDAO(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the category spinner
        val categories = Categories.values().map { it.name }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = spinnerAdapter

        // Open gallery to pick image
        binding.buttonPickImage.setOnClickListener {
            openGallery()
        }

        binding.buttonSaveProduct.setOnClickListener {
            val name = binding.editTextProductName.text.toString()
            val category = Categories.valueOf(binding.spinnerCategory.selectedItem.toString())

            // Check if an image is selected
            selectedImageUri?.let { uri ->
                // Save the product with the selected image URI
                val newProduct = Product(name, uri.toString(), category)
                productDAO.addProduct(newProduct)

                // Navigate back to the first fragment
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImageContent.launch(intent)
    }

    private fun loadImage(imageUri: String) {
        // Load the image into the ImageView using Glide
        Glide.with(requireContext())
            .load(imageUri)
            .into(binding.imageViewProduct)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        productDAO.close()
    }
}
