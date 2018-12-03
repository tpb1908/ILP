package com.tpb.coinz.data.chat

import com.google.firebase.firestore.*
import com.tpb.coinz.Result
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.util.CompositeRegistration
import com.tpb.coinz.data.util.Conversion
import com.tpb.coinz.data.util.FireStoreRegistration
import com.tpb.coinz.data.util.Registration
import timber.log.Timber

class FireStoreChatCollection(private val store: FirebaseFirestore) : ChatCollection {

    private var openThread: Thread? = null
    private var newMessageListener: ((Result<List<Message>>) -> Unit)? = null

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
                        "participants" to listOf(creator.uid, partner.uid),
                        "participant_emails" to listOf(creator.email, partner.email),
                        "created" to System.currentTimeMillis(),
                        "last_updated" to System.currentTimeMillis(),
                        "creator" to creator.uid, "creator_email" to creator.email,
                        "recipient" to partner.uid, "recipient_email" to partner.email
                )
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("Created thread $threadId")
                callback(Result.Value(Thread(threadId, creator, partner, System.currentTimeMillis())))

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
            Timber.i("Coin: ${doc.contains("coin")}")
            newMessages.add(Message(
                    doc["timestamp"] as Long,
                    User(
                            (doc["sender"] as Map<String, Any>)["uid"] as String,
                            (doc["sender"] as Map<String, Any>)["email"] as String
                    ),
                    doc["message"] as String,
                    if (doc.contains("coin") && doc["coin"] != null) Conversion.fromMap(doc["coin"] as Map<String, Any>) else null
            ))
        }
        Timber.i("New messages $newMessages")
        newMessages.sortBy { it.timestamp }
        newMessageListener?.invoke(Result.Value(newMessages))
    }

    private class ThreadsSnapshotListener(val threadsListener: ((Result<List<Thread>>) -> Unit)) : EventListener<QuerySnapshot> {

        override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
            Timber.i("Change to threads")
            val threads = mutableListOf<Thread>()
            //TODO: Deletion and proper change notification
            //TODO: How do we merge the two sources of the threads?
            snapshot?.documentChanges?.forEach { change ->
                if (change.type == DocumentChange.Type.ADDED) {
                    val doc = change.document
                    Timber.i("Thread downloaded $doc")
                    val participants = doc["participants"] as List<String>
                    val emails = doc["participant_emails"] as List<String>
                    threads.add(Thread(doc.id,

                            User(participants[0], emails[0]),
                            User(participants[1], emails[1]),
                            if (doc.contains("last_updated")) doc["last_updated"] as Long else doc["created"] as Long))
                } else {
                    //TODO: For the moment, this should never happen, as we have not way to delete threads
                    Timber.e("Document changed $change")
                }
            }
            threadsListener.invoke(Result.Value(threads))
        }
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
            store.collection(threads).document(it.threadId).set(mapOf("last_updated" to message.timestamp), SetOptions.merge())
        }
    }

    override fun openThreads(user: User, listener: (Result<List<Thread>>) -> Unit): Registration {
        val query = store.collection(threads).whereArrayContains("participants", user.uid)
        Timber.i("Getting threads for user $user")
        return FireStoreRegistration(query.addSnapshotListener(ThreadsSnapshotListener(listener)))
    }

    override fun openRecentThreads(user: User, count: Int, listener: (Result<List<Thread>>) -> Unit): Registration {
        // This query relies on an index
        // The composite index is on the participants (user uids) array contents, and the last_updated time in
        // descending order
        // If the index doesn't exist, the snapshot listener will be called once, but updates will not be received
        // https://github.com/invertase/react-native-firebase/issues/568
        val query = store.collection(threads).whereArrayContains("participants", user.uid).orderBy("last_updated", Query.Direction.DESCENDING).limit(count.toLong())
        Timber.i("Getting recent threads for user $user")
        return FireStoreRegistration(query.addSnapshotListener(ThreadsSnapshotListener(listener)))

    }

}