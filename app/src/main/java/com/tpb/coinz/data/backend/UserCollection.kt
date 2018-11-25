package com.tpb.coinz.data.backend

import com.tpb.coinz.Result

interface UserCollection {

    fun getCurrentUser(): User

    fun retrieveUsers(callback: (Result<List<User>>) -> Unit)

    fun retrieveUserFromEmail(email: String, callback: (Result<User>) -> Unit)

    data class User(val uid: String, val email: String)

}