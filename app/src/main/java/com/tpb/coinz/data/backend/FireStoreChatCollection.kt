package com.tpb.coinz.data.backend

import com.google.firebase.firestore.*
import com.tpb.coinz.Result
import timber.log.Timber

class FireStoreChatCollection(private val store: FirebaseFirestore) : ChatCollection {

    private var openThread: ChatCollection.Thread? = null
    private var messageListenerRegistration: ListenerRegistration? = null
    private var newMessageListener: ((Result<List<ChatCollection.Message>>) -> Unit)? = null
    private var threadsListener: ((Result<List<ChatCollection.Thread>>) -> Unit)? = null
    private var threadsListenerRegistration: Pair<ListenerRegistration, ListenerRegistration>? = null

    private inline fun messages(thread: ChatCollection.Thread) = store.collection(threads).document(thread.threadId).collection("messages")

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


    private val threadSnapshotListener: EventListener<QuerySnapshot> = EventListener { snapshot, exception ->
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

    private val threadsSnapshotListener: EventListener<QuerySnapshot> = EventListener { snapshot, exception ->
        val threads = mutableListOf<ChatCollection.Thread>()
        //TODO: How do we merge the two sources of the threads?
        snapshot?.documentChanges?.forEach { change ->
            if (change.type == DocumentChange.Type.ADDED) {
                val doc = change.document
                Timber.i("Thread downloaded $doc")
                threads.add(ChatCollection.Thread(doc.id,
                        UserCollection.User(
                                doc["creator"] as String,
                                doc["creator_email"] as String
                        ),
                        UserCollection.User(
                                doc["recipient"] as String,
                                doc["recipient_email"] as String
                        )))
            } else {
                //TODO: For the moment, this should never happen
                Timber.e("Document changed $change")
            }
        }
        Timber.i("Returning threads $threads")
        threadsListener?.invoke(Result.Value(threads))
    }

    override fun openThread(thread: ChatCollection.Thread, listener: (Result<List<ChatCollection.Message>>) -> Unit) {
        openThread = thread
        newMessageListener = listener
        messageListenerRegistration?.remove()
        messageListenerRegistration = messages(thread).addSnapshotListener(threadSnapshotListener)
    }

    override fun closeThread(thread: ChatCollection.Thread) {
        messageListenerRegistration?.remove()
    }

    override fun postMessage(message: ChatCollection.Message, callback: (Result<Boolean>) -> Unit) {
        openThread?.let {
            //TODO: Error handling
            messages(it).add(message)
        }
    }

    override fun openThreads(user: UserCollection.User, callback: (Result<List<ChatCollection.Thread>>) -> Unit) {
        val creatorQuery = store.collection(threads).whereEqualTo("creator", user.uid)
        val recipientQuery = store.collection(threads).whereEqualTo("recipient", user.uid)
        threadsListener = callback
        threadsListenerRegistration = Pair(
                creatorQuery.addSnapshotListener(threadsSnapshotListener),
                recipientQuery.addSnapshotListener(threadsSnapshotListener)
        )

        Timber.i("Getting threads for user $user")
    }

    override fun closeThreads() {
        threadsListenerRegistration?.first?.remove()
        threadsListenerRegistration?.second?.remove()
    }
}