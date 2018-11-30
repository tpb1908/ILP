package com.tpb.coinz.data.backend

import android.os.Parcelable
import com.tpb.coinz.Result
import kotlinx.android.parcel.Parcelize

interface UserCollection {

    fun getCurrentUser(): User

    fun retrieveUsers(callback: (Result<List<User>>) -> Unit)

    fun retrieveUserFromEmail(email: String, callback: (Result<User>) -> Unit)

    fun searchUsersByEmail(partialEmail: String, callback: (Result<List<User>>) -> Unit)

    @Parcelize
    data class User(val uid: String, val email: String) : Parcelable

}