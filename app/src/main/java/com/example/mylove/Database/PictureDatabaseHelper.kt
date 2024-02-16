package com.example.mylove.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mylove.Adapter.PictureData

class PictureDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "my_database"
        private const val TABLE_NAME = "Pictures"

        private const val KEY_ID = "id"
        private const val KEY_IMAGE = "image"
        private const val KEY_ABOUT = "about"
        private const val COLUMN_FRAGMENT_TAG = "fragment_tag"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_IMAGE BLOB, $COLUMN_FRAGMENT_TAG TEXT ,$KEY_ABOUT TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addPictures(picture: PictureData) : Long{
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(KEY_IMAGE, picture.image)
            put(COLUMN_FRAGMENT_TAG, picture.type)
            put(KEY_ABOUT, picture.about)
        }
        val id = db.insert(TABLE_NAME, null, cv)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getAllPictures() : List<PictureData>{
        val pictureList = mutableListOf<PictureData>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getLong(cursor.getColumnIndex(KEY_ID))
                val image = cursor.getString(cursor.getColumnIndex(KEY_IMAGE))
                val type = cursor.getString(cursor.getColumnIndex(COLUMN_FRAGMENT_TAG))
                val about = cursor.getString(cursor.getColumnIndex(KEY_ABOUT))

                val picture = PictureData(id, image, type, about)
                pictureList.add(picture)
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return pictureList
    }

    @SuppressLint("Range")
    fun getPicturesByFragment(fragmentTag: String): List<PictureData> {
        val pictureList = mutableListOf<PictureData>()
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_FRAGMENT_TAG = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(fragmentTag))

        if (cursor.moveToFirst()){
            do {
                val id = cursor.getLong(cursor.getColumnIndex(KEY_ID))
                val image = cursor.getString(cursor.getColumnIndex(KEY_IMAGE))
                val type = cursor.getString(cursor.getColumnIndex(COLUMN_FRAGMENT_TAG))
                val about = cursor.getString(cursor.getColumnIndex(KEY_ABOUT))

                val picture = PictureData(id, image, type, about)
                pictureList.add(picture)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return pictureList
    }

    fun deletePicture(vehicleId : Long) : Boolean{
        val db = writableDatabase
        try {
            val rowsDeleted = db.delete(TABLE_NAME, "$KEY_ID = ?", arrayOf(vehicleId.toString()))
            db.close()
            return rowsDeleted > 0
        }catch (e:Exception){
            db.close()
            return false
        }
    }
}