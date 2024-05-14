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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private lateinit var productDAO: ProductDAO
    private var selectedImageUri: Uri? = null
    private var productToEdit: Product? = null
    private var editMode: Boolean = false

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
        val categories = Categories.entries.map { it.name }
        val categorySpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categorySpinnerAdapter

        // Set up the states spinner
        val states = States.entries.map { it.name }
        val statesSpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, states)
        statesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerState.adapter = statesSpinnerAdapter

        arguments?.let {
            editMode = it.getBoolean("edit_mode", false)
            productToEdit = it.getParcelable("product")
            productToEdit?.let { product ->
                binding.editTextProductName.setText(product.name)
                selectedImageUri = Uri.parse(product.image)
                loadImage(product.image)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.editTextExpirationDate.setText(dateFormat.format(Date(product.expirationDate)))
                binding.editTextQuantity.setText(product.amount.toString())
                binding.spinnerCategory.setSelection(categories.indexOf(product.category.name))
                binding.spinnerState.setSelection(states.indexOf(product.state.name))
                binding.switchDiscarded.isChecked = product.isDiscarded
            }
        }

        // Open gallery to pick image
        binding.buttonPickImage.setOnClickListener {
            openGallery()
        }
        if (this.editMode)
        {
            binding.buttonSaveProduct.visibility = View.GONE
            binding.buttonSaveProduct.isEnabled = false
            binding.buttonEditProduct.setOnClickListener {
                val name = binding.editTextProductName.text.toString()
                val category = Categories.valueOf(binding.spinnerCategory.selectedItem.toString())
                val dateStr = binding.editTextExpirationDate.text.toString()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val validToTimestamp = dateFormat.parse(dateStr)?.time ?: 0L
                val amount = binding.editTextQuantity.text.toString().toIntOrNull() ?: 0
                val state = States.valueOf(binding.spinnerState.selectedItem.toString())
                val isDiscarded = binding.switchDiscarded.isChecked

                // Update existing product
                productToEdit?.let { product ->
                    product.name = name
                    product.image = selectedImageUri.toString()
                    product.category = category
                    product.expirationDate = validToTimestamp
                    product.amount = amount
                    product.state = state
                    product.isDiscarded = isDiscarded

                    productDAO.updateProduct(product)
                }
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }
        else
        {
            binding.buttonEditProduct.visibility = View.GONE
            binding.buttonEditProduct.isEnabled = false
            binding.buttonSaveProduct.setOnClickListener {
                val name = binding.editTextProductName.text.toString()
                val category = Categories.valueOf(binding.spinnerCategory.selectedItem.toString())
                val dateStr = binding.editTextExpirationDate.text.toString()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val validToTimestamp = dateFormat.parse(dateStr)?.time ?: 0L
                val amount = binding.editTextQuantity.text.toString().toIntOrNull() ?: 0
                val state = States.valueOf(binding.spinnerState.selectedItem.toString())
                val isDiscarded = binding.switchDiscarded.isChecked

                selectedImageUri?.let { uri ->
                    //TODO delete it later probably
                }
                val newProduct = Product(id, name, selectedImageUri.toString(), category, validToTimestamp, amount, state, isDiscarded )
                productDAO.addProduct(newProduct)

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
