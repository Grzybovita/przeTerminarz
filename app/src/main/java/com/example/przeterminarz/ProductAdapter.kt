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
    private val itemClickListener: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.iv_product)
        val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        val productCategory: TextView = itemView.findViewById(R.id.tv_product_category)
        val productExpirationDate: TextView = itemView.findViewById(R.id.tv_product_expiration_date)
        val productAmount: TextView = itemView.findViewById(R.id.tv_product_amount)
        val productState: TextView = itemView.findViewById(R.id.tv_product_state)
        val productIsDiscarded: TextView = itemView.findViewById(R.id.tv_product_is_discarded)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.product_view,
            parent, false
        )
        return ProductViewHolder(viewLayout)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = productList[position]
        Glide.with(holder.itemView.context)
            .load(currentProduct.image)
            .into(holder.productImage)
        holder.productName.text = currentProduct.name
        holder.productCategory.text = currentProduct.category.name
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.productExpirationDate.text = dateFormat.format(Date(currentProduct.expirationDate))
        holder.productAmount.text = currentProduct.amount.toString()
        holder.productState.text = currentProduct.state.name
        holder.productIsDiscarded.text = if (currentProduct.isDiscarded) "Discarded" else "Not Discarded"

        holder.itemView.setOnClickListener {
            itemClickListener(currentProduct)
        }
    }

    fun filterByCategories(categories: HashSet<Categories>) : Int {
        productList = productDAO.getAllProducts() as ArrayList<Product>;
        //TODO optimize, prepare sql statement with WHERE clause
        productList = productList.filter { categories.contains(it.category) } as ArrayList<Product>
        notifyDataSetChanged()
        return productList.size;
    }
}
