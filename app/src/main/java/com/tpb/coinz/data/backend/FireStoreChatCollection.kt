package com.tpb.coinz.data.backend

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.tpb.coinz.Result
import com.tpb.coinz.db.threads
import timber.log.Timber

class FireStoreChatCollection(private val store: FirebaseFirestore) : ChatCollection {

    private var openThread: ChatCollection.Thread? = null
    private var listenerRegistration: ListenerRegistration? = null
    private var newMessageListener: ((Result<List<ChatCollection.Message>>) -> Unit)? = null

    override fun createThread(creator: UserCollection.User,
                              partner: UserCollection.User,
                              callback: (Result<ChatCollection.Thread>) -> Unit) {
        // Create empty thread in threads
        // Then Add thread id to both users
        val collection = store.collection(threads)
        val threadId = "${creator.uid}|${partner.uid}"
        Timber.i("Creating thread for $threadId")
        collection.document(threadId).set(
                mapOf(
                        "created" to System.currentTimeMillis(),
                        "creator" to creator.uid, "creator_email" to creator.email,
                        "recipient" to partner.uid, "recipient_email" to partner.email
                )
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("Created thread $threadId")
                callback(Result.Value(ChatCollection.Thread(threadId, creator, partner)))

            } else if (it.isCanceled) {
                Timber.e("Cancelled thread creation $threadId")
                callback(Result.None)
            }
        }
    }


    private val tsl: EventListener<QuerySnapshot> = EventListener { snapshot, exception ->
        val newMessages = mutableListOf<ChatCollection.Message>()
        snapshot?.documentChanges?.forEach {
            Timber.i("Document change ${it.document}")
            val doc = it.document
            newMessages.add(ChatCollection.Message(
                    doc["timestamp"] as Long,
                    UserCollection.User(
                            (doc["sender"] as Map<String, Any>)["uid"] as String,
                            (doc["sender"] as Map<String, Any>)["email"] as String
                    ),
                    doc["message"] as String
            ))
        }
        Timber.i("New messages $newMessages")
        newMessages.sortBy { it.timestamp }
        newMessageListener?.invoke(Result.Value(newMessages))
    }

    override fun openThread(thread: ChatCollection.Thread, listener: (Result<List<ChatCollection.Message>>) -> Unit) {
        openThread = thread
        newMessageListener = listener
        listenerRegistration?.remove()
        listenerRegistration = store.collection(threads).document(thread.threadId).collection("messages").addSnapshotListener(tsl)
    }

    override fun closeThread(thread: ChatCollection.Thread) {
        listenerRegistration?.remove()
    }

    override fun postMessage(message: ChatCollection.Message, callback: (Result<Boolean>) -> Unit) {
        openThread?.let {
            //TODO: Error handling
            store.collection(threads).document(it.threadId).collection("messages").add(message)
        }
    }

    override fun getThreads(user: UserCollection.User, callback: (Result<List<ChatCollection.Thread>>) -> Unit) {
        val creatorQuery = store.collection(threads).whereEqualTo("creator", user.uid)
        val recipientQuery = store.collection(threads).whereEqualTo("recipient", user.uid)
        Timber.i("Getting threads for user $user")
        Tasks.whenAllComplete(creatorQuery.get(), recipientQuery.get()).addOnCompleteListener {
            val threads = mutableListOf<ChatCollection.Thread>()
            it.result?.forEach {task ->
                Timber.i("Merged query task $task, ${task.isSuccessful}, ${task.result}")
                if (task.isSuccessful && task.result is QuerySnapshot) {
                    (task.result as QuerySnapshot).documents.forEach {data ->
                       Timber.i("Thread downloaded $data")
                        threads.add(ChatCollection.Thread(data.id,
                                UserCollection.User(
                                        data["creator"] as String,
                                        data["creator_email"] as String
                                ),
                                UserCollection.User(
                                        data["recipient"] as String,
                                        data["recipient_email"] as String
                                )))
                    }
                }
            }
            callback(Result.Value(threads))
        }
    }


}