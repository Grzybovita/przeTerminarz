package com.example.przeterminarz

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class ProductDAO(context: Context) {

  private val dbHelper = ProductDatabaseHelper(context)
  private val database: SQLiteDatabase = dbHelper.writableDatabase

  fun addProduct(product: Product): Long {
    val values = ContentValues().apply {
      put(ProductDatabaseHelper.COLUMN_NAME, product.name)
      put(ProductDatabaseHelper.COLUMN_IMAGE, product.image)
      put(ProductDatabaseHelper.COLUMN_CATEGORY, product.category.name)
    }
    return database.insert(ProductDatabaseHelper.TABLE_NAME, null, values)
  }

  fun getAllProducts(): List<Product> {
    val productList = mutableListOf<Product>()
    val cursor: Cursor = database.query(
      ProductDatabaseHelper.TABLE_NAME,
      null,
      null,
      null,
      null,
      null,
      null
    )
    with(cursor) {
      while (moveToNext()) {
        val name = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_NAME))
        val image = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_IMAGE))
        val category = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_CATEGORY))
        val product = Product(name, image, Categories.valueOf(category))
        productList.add(product)
      }
    }
    cursor.close()
    return productList
  }


  fun close() {
    dbHelper.close()
  }
}