package com.tpb.coinz.data.chat

import android.os.Parcelable
import com.tpb.coinz.data.users.User
import kotlinx.android.parcel.Parcelize

/**
 * Data class for threads used in [ChatCollection]
 * @param threadId A unique identifier for the thread
 * @param creator The user that created the thread
 * @param partner The user that the creator is sending messages to
 */
@Parcelize
data class Thread(val threadId: String, val creator: User, val partner: User, val updated: Long) : Parcelable {

    /**
     * Given one [User] from the [Thread], returns the other [User]
     */
    fun otherUser(user: User): User = if (user == creator) partner else creator

}