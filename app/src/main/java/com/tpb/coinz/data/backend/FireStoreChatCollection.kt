package com.tpb.coinz.data.backend

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tpb.coinz.Result
import com.tpb.coinz.db.chats
import com.tpb.coinz.db.threads

class FireStoreChatCollection(private val store: FirebaseFirestore) : ChatCollection {

    private val user = FirebaseAuth.getInstance().currentUser

    //TODO: Add partner email
    override fun createThread(partnerId: String, callback: (com.tpb.coinz.Result<String>) -> Unit) {
        // Create empty thread in threads
        // Then Add thread id to both users
        val collection = store.collection(threads)
        val threadId = "${user?.uid}|$partnerId"
        Log.i("FireStoreChatCollection", "Creating thread for $threadId")
        collection.add(mapOf(threadId to mapOf("created" to System.currentTimeMillis(), "recipient" to partnerId))).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("FireStoreChatCollection", "Created thread $threadId")
                addThreadToUserChats(threadId, partnerId, callback)

            } else if (it.isCanceled) {
                Log.e("FireStoreChatCollection", "Cancelled thread creation $threadId")
            }
        }
    }

    private fun addThreadToUserChats(threadId: String, partnerId: String, callback: (com.tpb.coinz.Result<String>) -> Unit) {
        val userId = user?.uid ?: "user_id_error"
        val chats = store.collection(chats)
        val userDoc = chats.document(userId)
        val partnerDoc = chats.document(partnerId)
        store.runTransaction {
            it.set(userDoc, mapOf(threadId to partnerId), SetOptions.merge())
            it.set(partnerDoc, mapOf(threadId to userId))
            callback(Result.Value(threadId))
        }
    }

    override fun openThread(partnerId: String) {

    }

    override fun closeThread(partnerId: String) {
    }

    override fun postMessage(message: String) {
    }

    override fun getThreads() {
        val userId = user?.uid ?: "user_id_error"
        store.collection(chats).document(userId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.data?.let { data ->
                    data.keys.forEach { key ->
                        Log.i("FireStoreChatCollection", "Thread $key, ")
                    }
                }
            } else {
                Log.e("FireStoreChatCollection", "Error getting threads ", it.exception)
            }
        }
    }
}