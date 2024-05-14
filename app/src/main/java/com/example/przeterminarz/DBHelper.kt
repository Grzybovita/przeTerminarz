/*
package com.example.przeterminarz

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
  SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

  override fun onCreate(db: SQLiteDatabase) {
    val query = ("CREATE TABLE " + PRODUCT_TABLE_NAME + " ("
            + PRODUCT_ID + " INTEGER PRIMARY KEY, " +
            PRODUCT_NAME + " TEXT," +
            PRODUCT_IMAGE + " TEXT" + ")")

    db.execSQL(query)


  }

  override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
    db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE_NAME)
    onCreate(db)
  }

  fun addName(name : String, image : String ){
    val values = ContentValues()
    values.put(PRODUCT_NAME, name)
    values.put(PRODUCT_IMAGE, image)
    val db = this.writableDatabase
    db.insert(PRODUCT_TABLE_NAME, null, values)
    db.close()
  }

  fun getName(): Cursor? {
    val db = this.readableDatabase
    return db.rawQuery("SELECT * FROM " + PRODUCT_TABLE_NAME, null)
  }

  companion object{
    private val DATABASE_NAME = "PrzeTerminarzDB"
    private val DATABASE_VERSION = 1
    val PRODUCT_TABLE_NAME = "products"
    val PRODUCT_ID = "id"
    val PRODUCT_NAME = "name"
    val PRODUCT_IMAGE = "image"
  }
}*/
