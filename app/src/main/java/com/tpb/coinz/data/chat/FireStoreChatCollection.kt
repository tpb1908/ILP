package com.tpb.coinz.data.chat

import com.google.firebase.firestore.*
import com.tpb.coinz.CompositeRegistration
import com.tpb.coinz.FireStoreRegistration
import com.tpb.coinz.Registration
import com.tpb.coinz.Result
import com.tpb.coinz.data.users.User
import timber.log.Timber

class FireStoreChatCollection(private val store: FirebaseFirestore) : ChatCollection {

    private var openThread: Thread? = null
    private var newMessageListener: ((Result<List<Message>>) -> Unit)? = null
    private var threadsListener: ((Result<List<Thread>>) -> Unit)? = null

    private val threads = "threads"

    private inline fun messages(thread: Thread) = store.collection(threads).document(thread.threadId).collection("messages")

    override fun createThread(creator: User,
                              partner: User,
                              callback: (Result<Thread>) -> Unit) {
        val threadId = "${creator.uid}|${partner.uid}"
        Timber.i("Creating thread for $threadId")
        // Create thread document
        store.collection(threads).document(threadId).set(
                mapOf(
                        "created" to System.currentTimeMillis(),
                        "creator" to creator.uid, "creator_email" to creator.email,
                        "recipient" to partner.uid, "recipient_email" to partner.email
                )
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("Created thread $threadId")
                callback(Result.Value(Thread(threadId, creator, partner)))

            } else  {
                Timber.e(it.exception, "Failed thread creation $threadId")
                callback(Result.None)
            }
        }
    }

    // Listener for the thread we are watching
    private val threadSnapshotListener: EventListener<QuerySnapshot> = EventListener { snapshot, exception ->
        val newMessages = mutableListOf<Message>()
        snapshot?.documentChanges?.forEach {
            Timber.i("Document change ${it.document}")
            val doc = it.document
            newMessages.add(Message(
                    doc["timestamp"] as Long,
                    User(
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
        val threads = mutableListOf<Thread>()
        //TODO: How do we merge the two sources of the threads?
        snapshot?.documentChanges?.forEach { change ->
            if (change.type == DocumentChange.Type.ADDED) {
                val doc = change.document
                Timber.i("Thread downloaded $doc")
                threads.add(Thread(doc.id,
                        User(
                                doc["creator"] as String,
                                doc["creator_email"] as String
                        ),
                        User(
                                doc["recipient"] as String,
                                doc["recipient_email"] as String
                        )))
            } else {
                //TODO: For the moment, this should never happen, as we have not way to delete threads
                Timber.e("Document changed $change")
            }
        }
        Timber.i("Returning threads $threads")
        threadsListener?.invoke(Result.Value(threads))
    }

    override fun openThread(thread: Thread, listener: (Result<List<Message>>) -> Unit): Registration {
        openThread = thread
        newMessageListener = listener
        return FireStoreRegistration(messages(thread).addSnapshotListener(threadSnapshotListener))
    }


    override fun postMessage(message: Message, callback: (Result<Boolean>) -> Unit) {
        openThread?.let {
            //TODO: Error handling
            messages(it).add(message)
        }
    }

    override fun openThreads(user: User, listener: (Result<List<Thread>>) -> Unit): Registration {
        val creatorQuery = store.collection(threads).whereEqualTo("creator", user.uid)
        val recipientQuery = store.collection(threads).whereEqualTo("recipient", user.uid)
        threadsListener = listener
        Timber.i("Getting threads for user $user")
        return CompositeRegistration(mutableListOf(
                FireStoreRegistration(creatorQuery.addSnapshotListener(threadsSnapshotListener)),
                FireStoreRegistration(recipientQuery.addSnapshotListener(threadsSnapshotListener)))
        )


    }
}