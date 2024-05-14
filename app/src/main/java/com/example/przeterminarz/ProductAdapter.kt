package com.example.przeterminarz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter (private val productList : ArrayList<Product>): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

    inner class ProductViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val productImage : ImageView = itemView.findViewById(R.id.iv_product)
        val productName : TextView = itemView.findViewById(R.id.tv_product_text)
    }

    private var filteredProductList: List<Product> = productList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val viewLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.product_view,
            parent,false)
        return ProductViewHolder(viewLayout)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = productList[position]
        holder.productImage.setImageResource(currentProduct.image)
        holder.productName.text = currentProduct.name
    }

    fun filterByCategory(category: Categories) {
        filteredProductList = productList.filter { it.category == category }
        notifyDataSetChanged()
    }
}

