package com.example.loicratio

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + idDiscord + " VARCHAR(255) PRIMARY KEY, " +
                nameDiscord + " VARCHAR(255)" +
                ");")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addKickDiscord(idD : String, nameD : String ){
        val values = ContentValues()
        values.put(idDiscord, idD)
        values.put(nameDiscord, nameD)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
    }
    fun removeKickDiscord(idD : String):Int{
        val db = this.writableDatabase

        val selection = "idDiscord = ?"
        val selectionArgs = arrayOf(idD)

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    @SuppressLint("Range")
    fun getAllKickDiscord(): ArrayList<KickDiscord> {
        val db = this.readableDatabase
        val res = ArrayList<KickDiscord>()

        val projection = arrayOf("idDiscord", "nameDiscord")

        val cursor = db.query(
            TABLE_NAME,   // La table sur laquelle faire la requête
            projection,   // Les colonnes à retourner
            null,         // Les colonnes pour la clause WHERE
            null,         // Les valeurs pour la clause WHERE
            null,         // group by
            null,         // having
            null          // order by
        )

        if (cursor.moveToFirst()) {
            do {
                val idDiscord = cursor.getString(cursor.getColumnIndex("idDiscord"))
                val nameDiscord = cursor.getString(cursor.getColumnIndex("nameDiscord"))
                res.add(KickDiscord(idDiscord, nameDiscord))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return res

    }

    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "KickDiscord"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "kickdiscord"

        // below is the variable for name column
        val idDiscord = "idDiscord"

        // below is the variable for age column
        val nameDiscord = "nameDiscord"
    }
}