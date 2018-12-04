package com.tpb.coinz.data.users


interface UserCollection {

    fun getCurrentUser(): User

    fun retrieveUsers(callback: (Result<List<User>>) -> Unit)

    fun retrieveUserFromEmail(email: String, callback: (Result<User>) -> Unit)

    fun searchUsersByEmail(partialEmail: String, callback: (Result<List<User>>) -> Unit)

}