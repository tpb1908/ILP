package com.tpb.coinz.data.backend

import android.util.Log
import com.firebase.ui.auth.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tpb.coinz.Result
import com.tpb.coinz.db.chats
import com.tpb.coinz.db.threads

class FireStoreChatCollection(private val store: FirebaseFirestore) : ChatCollection {


    override fun createThread(creator: UserCollection.User, partner: UserCollection.User, callback: (com.tpb.coinz.Result<ChatCollection.Thread>) -> Unit) {
        // Create empty thread in threads
        // Then Add thread id to both users
        val collection = store.collection(threads)
        val threadId = "${creator.uid}|${partner.uid}"
        Log.i("FireStoreChatCollection", "Creating thread for $threadId")
        collection.document(threadId).set(mapOf("created" to System.currentTimeMillis(), "recipient" to partner.uid, "recipient_email" to partner.email)).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("FireStoreChatCollection", "Created thread $threadId")
                addThreadToUserChats(threadId, creator, partner, callback)

            } else if (it.isCanceled) {
                Log.e("FireStoreChatCollection", "Cancelled thread creation $threadId")
                callback(Result.None)
            }
        }
    }

    private fun addThreadToUserChats(threadId: String, creator: UserCollection.User, partner: UserCollection.User, callback: (com.tpb.coinz.Result<ChatCollection.Thread>) -> Unit) {
        val chats = store.collection(chats)
        val userDoc = chats.document(creator.uid)
        val partnerDoc = chats.document(partner.uid)
        store.runTransaction {
            it.set(userDoc,
                    mapOf(threadId to mapOf("partner_uid" to partner.uid, "partner_email" to partner.email)),
                    SetOptions.merge()
            )
            it.set(partnerDoc,
                    mapOf(threadId to mapOf("partner_uid" to creator.uid, "partner_email" to creator.email)),
                    SetOptions.merge()
            )
            callback(Result.Value(ChatCollection.Thread(threadId, partner)))
        }
    }

    override fun openThread(creator: User, partner: User) {
    }

    override fun closeThread(partnerId: String) {
    }

    override fun postMessage(message: String) {
    }

    override fun getThreads(user: UserCollection.User, callback: (Result<List<ChatCollection.Thread>>) -> Unit) {
        store.collection(chats).document(user.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.data?.let { data ->
                    val threads = mutableListOf<ChatCollection.Thread>()
                    data.keys.forEach { key ->
                        threads.add(ChatCollection.Thread(key, UserCollection.User(
                                (data[key] as Map<String, Any>)["partner_uid"] as String,
                                (data[key] as Map<String, Any>)["partner_email"] as String
                        )))
                        Log.i("FireStoreChatCollection", "Thread $key, ")
                    }
                    callback(Result.Value(threads))
                }
            } else {
                Log.e("FireStoreChatCollection", "Error getting threads ", it.exception)
                callback(Result.None)
            }
        }
    }
}