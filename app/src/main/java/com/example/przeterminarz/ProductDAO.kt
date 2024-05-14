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
      put(ProductDatabaseHelper.COLUMN_EXPIRATION_DATE, product.expirationDate)
      put(ProductDatabaseHelper.COLUMN_AMOUNT, product.amount)
      put(ProductDatabaseHelper.COLUMN_STATE, product.state.name)
      put(ProductDatabaseHelper.COLUMN_IS_DISCARDED, product.isDiscarded)
    }
    return database.insert(ProductDatabaseHelper.TABLE_NAME, null, values)
  }

  fun updateProduct(product: Product): Int {
    val values = ContentValues().apply {
      put(ProductDatabaseHelper.COLUMN_NAME, product.name)
      put(ProductDatabaseHelper.COLUMN_IMAGE, product.image)
      put(ProductDatabaseHelper.COLUMN_CATEGORY, product.category.name)
      put(ProductDatabaseHelper.COLUMN_EXPIRATION_DATE, product.expirationDate)
      put(ProductDatabaseHelper.COLUMN_AMOUNT, product.amount)
      put(ProductDatabaseHelper.COLUMN_STATE, product.state.name)
      put(ProductDatabaseHelper.COLUMN_IS_DISCARDED, product.isDiscarded)
    }
    val selection = "${ProductDatabaseHelper.COLUMN_ID} = ?"
    val selectionArgs = arrayOf(product.id.toString())

    return database.update(
      ProductDatabaseHelper.TABLE_NAME,
      values,
      selection,
      selectionArgs
    )
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
        val id = getInt(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_ID))
        val name = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_NAME))
        val image = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_IMAGE))
        val category = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_CATEGORY))
        val expirationDate = getLong(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_EXPIRATION_DATE))
        val amount = getInt(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_AMOUNT))
        val state = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_STATE))
        val discarded = getInt(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_IS_DISCARDED)) > 0
        val product = Product(id, name, image, Categories.valueOf(category), expirationDate, amount, States.valueOf(state), discarded)
        productList.add(product)
      }
    }
    cursor.close()
    return productList
  }

  fun getFilteredProducts(categories: HashSet<Categories>): List<Product> {
    val productList = mutableListOf<Product>()
    if (categories.isEmpty()) {
      return productList
    }

    val categoryNames = categories.joinToString(",") { "'${it.name}'" }
    val selection = "${ProductDatabaseHelper.COLUMN_CATEGORY} IN ($categoryNames)"
    val cursor: Cursor = database.query(
      ProductDatabaseHelper.TABLE_NAME,
      null,
      selection,
      null,
      null,
      null,
      null
    )

    with(cursor) {
      while (moveToNext()) {
        val id = getInt(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_ID))
        val name = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_NAME))
        val image = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_IMAGE))
        val category = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_CATEGORY))
        val expirationDate = getLong(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_EXPIRATION_DATE))
        val amount = getInt(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_AMOUNT))
        val state = getString(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_STATE))
        val discarded = getInt(getColumnIndexOrThrow(ProductDatabaseHelper.COLUMN_IS_DISCARDED)) > 0
        val product = Product(id, name, image, Categories.valueOf(category), expirationDate, amount, States.valueOf(state), discarded)
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