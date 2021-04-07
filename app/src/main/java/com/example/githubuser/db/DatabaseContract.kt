package com.example.githubuser.db


import android.net.Uri
import android.provider.BaseColumns

internal class DatabaseContract {




    internal class UserColumns : BaseColumns {

        companion object {
            const val TABLE_NAME = "favorite_user"
            const val _ID = "_id"
            const val COLUMN_NAME_USERNAME = "username"
            const val COLUMN_NAME_AVATAR_URL = "avatar_url"
            const val COLUMN_NAME_NAME = "name"
            const val COLUMN_NAME_FOLLOWWER = "follower"
            const val COLUMN_NAME_FOLLOWWING = "following"
            const val COLUMN_NAME_COMPANY = "company"
            const val COLUMN_NAME_LOCATION = "location"
            const val COLUMN_NAME_REPOSITORY = "repository"
        }
    }

}