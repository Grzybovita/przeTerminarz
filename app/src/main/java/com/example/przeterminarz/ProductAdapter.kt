package com.example.przeterminarz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(private var productList: ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.iv_product)
        val productName: TextView = itemView.findViewById(R.id.tv_product_text)
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
        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .load(currentProduct.image)
            .into(holder.productImage)
        holder.productName.text = currentProduct.name
    }

    fun filterByCategory(category: Categories) {
        productList = productList.filter { it.category == category } as ArrayList<Product>
        notifyDataSetChanged()
    }
}

