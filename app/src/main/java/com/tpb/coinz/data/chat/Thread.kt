package com.tpb.coinz.data.chat

import android.os.Parcelable
import com.tpb.coinz.data.users.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Thread(val threadId: String, val creator: User, val partner: User) : Parcelable {

    fun otherUser(user: User): User = if (user == creator) partner else creator

}