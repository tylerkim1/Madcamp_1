package com.example.test2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class CardDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_CARDS_TABLE = "CREATE TABLE $TABLE_CARDS($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_IMAGE_URI TEXT, $COLUMN_TEXT TEXT)"
        db.execSQL(CREATE_CARDS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CARDS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "CardDatabase"
        const val TABLE_CARDS = "Cards"
        const val COLUMN_ID = "id"
        const val COLUMN_IMAGE_URI = "imageUri"
        const val COLUMN_TEXT = "text"
    }

    fun addCard(card: Card) {
        val values = ContentValues().apply {
            put(COLUMN_IMAGE_URI, card.imageUri.toString())
            put(COLUMN_TEXT, card.text)
        }

        val db = this.writableDatabase

        db.insert(TABLE_CARDS, null, values)
        db.close()
    }

    fun deleteCard(card: Card) {
        val db = this.writableDatabase
        val selectionArgs = arrayOf(card.imageUri.toString(), card.text)
        db.delete(TABLE_CARDS, "$COLUMN_IMAGE_URI = ? AND $COLUMN_TEXT = ?", selectionArgs)
        db.close()
    }

    fun updateCardText(newText: String, oldCard: Card) {
        val values = ContentValues().apply {
            put(COLUMN_IMAGE_URI, oldCard.imageUri.toString())
            put(COLUMN_TEXT, newText)
        }

        val db = this.writableDatabase

        val selectionArgs = arrayOf(oldCard.imageUri.toString(), oldCard.text)
        db.update(TABLE_CARDS, values, "$COLUMN_IMAGE_URI = ? AND $COLUMN_TEXT = ?", selectionArgs)

        db.close()
    }

    @SuppressLint("Range")
    fun getAllCards(): List<Card> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CARDS", null)

        val cards = mutableListOf<Card>()
        if (cursor.moveToFirst()) {
            do {
                val imageUri = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI))
                val text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT))
                val card = Card(Uri.parse(imageUri), text)
                cards.add(card)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return cards
    }


}
