package com.example.przeterminarz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProductDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

  companion object {
    const val DATABASE_NAME = "products.db"
    const val DATABASE_VERSION = 5
    const val TABLE_NAME = "products"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_IMAGE = "image"
    const val COLUMN_CATEGORY = "category"
    const val COLUMN_EXPIRATION_DATE = "expiration_date"
    const val COLUMN_AMOUNT = "amount"
    const val COLUMN_STATE = "state"
    const val COLUMN_IS_DISCARDED = "discarded"
    const val COLUMN_UNIT = "unit"
  }

  override fun onCreate(db: SQLiteDatabase)
  {
    db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

    val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_IMAGE INTEGER,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_EXPIRATION_DATE INTEGER,
                $COLUMN_AMOUNT INTEGER,
                $COLUMN_STATE TEXT,
                $COLUMN_IS_DISCARDED INTEGER,
                $COLUMN_UNIT TEXT
            )
        """
    db.execSQL(createTable)

    /*val initialData = """
            INSERT INTO $TABLE_NAME ($COLUMN_NAME, $COLUMN_IMAGE, $COLUMN_CATEGORY, $COLUMN_EXPIRATION_DATE, $COLUMN_AMOUNT, $COLUMN_STATE, $COLUMN_IS_DISCARDED)
            VALUES
            ('Apple', 1, 'GROCERIES', 1716336000000, 10, 'VALID', 0, "pieces"),
            ('Orange', 2, 'GROCERIES', 1716336000000, 10, 'VALID', 0, "pieces"),
            ('VitamineC', 2, 'MEDICINES', 1716595200000, 10, 'VALID', 0, "tabs"),
            ('VitamineD', 2, 'MEDICINES', 1716595200000, 10, 'VALID', 0, "tabs"),
            ('Shower Gel', 2, 'COSMETICS', 1716595200000, 10, 'VALID', 0, "bottles")
        """
    db.execSQL(initialData)*/
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
  {
    db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    onCreate(db)
  }
}