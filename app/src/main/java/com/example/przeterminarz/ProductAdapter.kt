package com.example.przeterminarz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductAdapter(
    private var productList: ArrayList<Product>,
    private val productDAO: ProductDAO,
    private val itemClickListener: (Product) -> Unit,
    private val itemLongClickListener: (Product) -> Unit // New long click listener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.iv_product)
        private val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        private val productCategory: TextView = itemView.findViewById(R.id.tv_product_category)
        private val productExpirationDate: TextView = itemView.findViewById(R.id.tv_product_expiration_date)
        private val productAmount: TextView = itemView.findViewById(R.id.tv_product_amount)
        private val productState: TextView = itemView.findViewById(R.id.tv_product_state)
        private val productIsDiscarded: TextView = itemView.findViewById(R.id.tv_product_is_discarded)

        fun bind(product: Product)
        {
            Glide.with(itemView.context)
                .load(product.image)
                .into(productImage)
            productName.text = product.name
            productCategory.text = product.category.name
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            productExpirationDate.text = dateFormat.format(Date(product.expirationDate))
            productAmount.text = product.amount.toString()
            productState.text = product.state.name
            productIsDiscarded.text = if (product.isDiscarded) "Discarded" else "Not Discarded"

            itemView.setOnClickListener {
                itemClickListener(product)
            }

            itemView.setOnLongClickListener {
                itemLongClickListener(product)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder
    {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.product_view,
            parent, false
        )
        return ProductViewHolder(viewLayout)
    }

    override fun getItemCount(): Int
    {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int)
    {
        val currentProduct = productList[position]
        holder.bind(currentProduct)
    }

    fun filterByCategoriesAndStates(categories: HashSet<Categories>, states: HashSet<States>): Int
    {
        productList = productDAO.getAllProducts() as ArrayList<Product>
        productList = productList.filter { categories.contains(it.category) } as ArrayList<Product>
        productList = productList.filter { states.contains(it.state) } as ArrayList<Product>
        notifyDataSetChanged()
        return productList.size
    }
}
