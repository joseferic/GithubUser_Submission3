package com.example.githubuser


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
        var id: Int?= 0,
        var photo: String?="",
        var UserName: String?="test",
        var Nama: String? ="",
        var Follower: String?="",
        var Following: String? ="",
        var Company: String? ="",
        var Location: String? ="",
        var Repository: String?="",
) : Parcelable
