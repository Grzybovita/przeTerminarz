package com.example.przeterminarz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProductDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

  companion object {
    const val DATABASE_NAME = "products.db"
    const val DATABASE_VERSION = 2
    const val TABLE_NAME = "products"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_IMAGE = "image"
    const val COLUMN_CATEGORY = "category"
    const val COLUMN_EXPIRATION_DATE = "expiration_date"
    const val COLUMN_AMOUNT = "amount"
    const val COLUMN_STATE = "state"
    const val COLUMN_IS_DISCARDED = "discarded"
  }

  override fun onCreate(db: SQLiteDatabase)
  {
    val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_IMAGE INTEGER,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_EXPIRATION_DATE INTEGER,
                $COLUMN_AMOUNT INTEGER,
                $COLUMN_STATE TEXT,
                $COLUMN_IS_DISCARDED INTEGER
            )
        """
    db.execSQL(createTable)
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
  {
    db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    onCreate(db)
  }
}