package com.example.przeterminarz

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.przeterminarz.databinding.FragmentAddProductBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
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
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        productDAO = ProductDAO(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //set category spinner
        val categories = Categories.entries.map { it.name }
        val categorySpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categorySpinnerAdapter

        //set states spinner
        val states = States.entries.map { it.name }
        val statesSpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, states)
        statesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerState.adapter = statesSpinnerAdapter

        //set unit spinner
        val units = listOf("", "bottles", "tabs", "units", "pieces")
        val unitSpinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        unitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnit.adapter = unitSpinnerAdapter

        binding.editTextExpirationDate.setOnClickListener {
            showDatePickerDialog()
        }

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
                binding.spinnerUnit.setSelection(units.indexOf(product.unit))
            }
        }

        binding.buttonPickImage.setOnClickListener {
            openGallery()
        }
        if (this.editMode)
        {
            activity?.title = getString(R.string.edit_product_fragment_label)
            binding.buttonSaveProduct.visibility = View.GONE
            binding.buttonSaveProduct.isEnabled = false
            binding.buttonEditProduct.setOnClickListener { updateProduct() }
        }
        else
        {
            activity?.title = getString(R.string.add_product_fragment_label)
            binding.buttonEditProduct.visibility = View.GONE
            binding.buttonEditProduct.isEnabled = false
            binding.buttonSaveProduct.setOnClickListener { saveProduct() }
        }
    }

    private fun showDatePickerDialog()
    {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.editTextExpirationDate.setText(dateFormat.format(selectedDate))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun saveProduct()
    {
        val name = binding.editTextProductName.text.toString()
        val category = Categories.valueOf(binding.spinnerCategory.selectedItem.toString())
        val dateStr = binding.editTextExpirationDate.text.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var expirationDateTimestamp = 0L
        if (dateStr != "")
        {
            expirationDateTimestamp = dateFormat.parse(dateStr)?.time!!
        }
        val amount = binding.editTextQuantity.text.toString().toIntOrNull() ?: 0
        val state = States.valueOf(binding.spinnerState.selectedItem.toString())
        val isDiscarded = binding.switchDiscarded.isChecked
        var unit = binding.spinnerUnit.selectedItem.toString()

        if (name.isEmpty())
        {
            Toast.makeText(requireContext(), R.string.validation_name_empty, Toast.LENGTH_SHORT).show()
            return
        }
        else if (amount <= 0)
        {
            unit = "";
            Toast.makeText(requireContext(), R.string.validation_quantity_not_valid, Toast.LENGTH_SHORT).show()
            return
        }
        else if (dateStr.isEmpty())
        {
            Toast.makeText(requireContext(), R.string.validation_date_empty, Toast.LENGTH_SHORT).show()
            return
        }
        else if (amount > 0 && unit.isEmpty())
        {
            Toast.makeText(requireContext(), R.string.validation_unit_empty, Toast.LENGTH_SHORT).show()
            return
        }

        val newProduct = Product(0, name, selectedImageUri.toString(), category, expirationDateTimestamp, amount, state, isDiscarded, unit)

        productDAO.addProduct(newProduct)
        findNavController().navigate(R.id.action_AddProductFragment_to_DisplayListFragment)
    }

    private fun updateProduct()
    {
        val name = binding.editTextProductName.text.toString()
        val category = Categories.valueOf(binding.spinnerCategory.selectedItem.toString())
        val dateStr = binding.editTextExpirationDate.text.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var expirationDateTimestamp = 0L
        if (dateStr != "")
        {
            expirationDateTimestamp = dateFormat.parse(dateStr)?.time!!
        }
        val amount = binding.editTextQuantity.text.toString().toIntOrNull() ?: 0
        val state = States.valueOf(binding.spinnerState.selectedItem.toString())
        val isDiscarded = binding.switchDiscarded.isChecked
        var unit = binding.spinnerState.selectedItem.toString()

        //TODO merge 'create' and 'update' validation, code redundancy
        if (name.isEmpty())
        {
            Toast.makeText(requireContext(), R.string.validation_name_empty, Toast.LENGTH_SHORT).show()
            return
        }
        else if (amount <= 0)
        {
            unit = "";
            Toast.makeText(requireContext(), R.string.validation_quantity_not_valid, Toast.LENGTH_SHORT).show()
            return
        }
        else if (dateStr.isEmpty())
        {
            Toast.makeText(requireContext(), R.string.validation_date_empty, Toast.LENGTH_SHORT).show()
            return
        }
        else if (amount > 0 && unit.isEmpty())
        {
            Toast.makeText(requireContext(), R.string.validation_unit_empty, Toast.LENGTH_SHORT).show()
            return
        }

        productToEdit?.let { product ->
            product.name = name
            product.image = selectedImageUri.toString()
            product.category = category
            product.expirationDate = expirationDateTimestamp
            product.amount = amount
            product.state = state
            product.isDiscarded = isDiscarded
            product.unit = unit
            productDAO.updateProduct(product)
            findNavController().navigate(R.id.action_AddProductFragment_to_DisplayListFragment)
        }
    }

    private fun openGallery()
    {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImageContent.launch(intent)
    }

    private fun loadImage(imageUri: String)
    {
        //Load the image into the ImageView using Glide
        Glide.with(requireContext())
            .load(imageUri)
            .into(binding.imageViewProduct)
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
        productDAO.close()
    }
}
