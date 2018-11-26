package com.tpb.coinz.data.backend

import android.util.Log
import com.google.firebase.firestore.*
import com.tpb.coinz.Result
import com.tpb.coinz.db.chats
import com.tpb.coinz.db.threads

class FireStoreChatCollection(private val store: FirebaseFirestore) : ChatCollection {

    private var openThread: ChatCollection.Thread? = null
    private var listenerRegistration: ListenerRegistration? = null
    private var messageChangeListener: ((Result<List<ChatCollection.Message>>) -> Unit)? = null

    override fun createThread(creator: UserCollection.User, partner: UserCollection.User, callback: (Result<ChatCollection.Thread>) -> Unit) {
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

    private fun addThreadToUserChats(threadId: String, creator: UserCollection.User, partner: UserCollection.User, callback: (Result<ChatCollection.Thread>) -> Unit) {
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
            callback(Result.Value(ChatCollection.Thread(threadId, creator, partner)))
        }
    }

    private val threadSnapshotListener: EventListener<DocumentSnapshot> = EventListener { snapshot, exception ->
        if (snapshot?.exists() == true) {
            snapshot.data?.let {
                Log.i("FireStoreChatCollection", "Chat data recieved $it")
                messageChangeListener?.invoke(Result.None)
            }
        }
    }

    override fun openThread(thread: ChatCollection.Thread, listener: (Result<List<ChatCollection.Message>>) -> Unit) {
        openThread = thread
        listenerRegistration?.remove()
        listenerRegistration = store.collection(threads).document(thread.threadId).addSnapshotListener(threadSnapshotListener)
    }

    override fun closeThread(thread: ChatCollection.Thread) {
        listenerRegistration?.remove()
    }

    override fun postMessage(message: ChatCollection.Message, callback: (Result<Boolean>) -> Unit) {
        openThread?.let {
            store.collection(threads).document(it.threadId)
        }
    }

    override fun getThreads(user: UserCollection.User, callback: (Result<List<ChatCollection.Thread>>) -> Unit) {
        store.collection(chats).document(user.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.data?.let { data ->
                    val threads = mutableListOf<ChatCollection.Thread>()
                    data.keys.forEach { key ->
                        threads.add(ChatCollection.Thread(key, user, UserCollection.User(
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