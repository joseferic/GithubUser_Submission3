package com.example.githubuser.helper

import android.database.Cursor
import com.example.githubuser.Users
import com.example.githubuser.db.DatabaseContract

object MappingHelper {
    fun mapCursorToArrayList(userCursor: Cursor?): ArrayList<Users>{
        val userFavoriteList = ArrayList<Users>()

        userCursor?.apply {
            while (moveToNext()){
                var id = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns._ID))
                var photo= getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COLUMN_NAME_AVATAR_URL))
                var UserName = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COLUMN_NAME_USERNAME))
                var Nama = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COLUMN_NAME_NAME))
                var Follower = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COLUMN_NAME_FOLLOWWER))
                var Following = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COLUMN_NAME_FOLLOWWING))
                var Company = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COLUMN_NAME_COMPANY))
                var Location = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COLUMN_NAME_LOCATION))
                var Repository = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.COLUMN_NAME_REPOSITORY))
                userFavoriteList.add(Users(id,photo,UserName,Nama,Follower,Following,Company,Location,Repository))
            }
        }
        return userFavoriteList
    }
}