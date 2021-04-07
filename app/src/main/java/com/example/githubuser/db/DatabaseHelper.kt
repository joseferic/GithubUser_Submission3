package com.example.githubuser.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.githubuser.db.DatabaseContract.UserColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbUserFavoriteApp"

        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_TABLE_FAVORITE = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseContract.UserColumns._ID} INTEGER PRIMARY KEY," +
                " ${DatabaseContract.UserColumns.COLUMN_NAME_USERNAME} TEXT NOT NULL," +
                " ${DatabaseContract.UserColumns.COLUMN_NAME_AVATAR_URL} TEXT NOT NULL," +
                " ${DatabaseContract.UserColumns.COLUMN_NAME_NAME} TEXT NOT NULL," +
                " ${DatabaseContract.UserColumns.COLUMN_NAME_FOLLOWWER} TEXT NOT NULL," +
                " ${DatabaseContract.UserColumns.COLUMN_NAME_FOLLOWWING} TEXT NOT NULL," +
                " ${DatabaseContract.UserColumns.COLUMN_NAME_COMPANY} TEXT NOT NULL," +
                " ${DatabaseContract.UserColumns.COLUMN_NAME_LOCATION} TEXT NOT NULL," +
                " ${DatabaseContract.UserColumns.COLUMN_NAME_REPOSITORY} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_FAVORITE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


}