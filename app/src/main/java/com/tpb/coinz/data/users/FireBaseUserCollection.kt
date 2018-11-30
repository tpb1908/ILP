package com.tpb.coinz.data.users

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.Result
import timber.log.Timber

class FireBaseUserCollection(private val store: FirebaseFirestore) : UserCollection {

    private val users = "users"
    private val email = "email"

    override fun getCurrentUser(): User {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return User(firebaseUser?.uid ?: "uid_error", firebaseUser?.email ?: "email_error")
    }

    override fun retrieveUsers(callback: (Result<List<User>>) -> Unit) {
        store.collection(users).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val users = mutableListOf<User>()
                it.result?.documents?.forEach { doc ->
                    users.add(User(doc.id, doc.getString(email) ?: ""))
                }
                callback(Result.Value(users))
            }
        }
    }

    override fun retrieveUserFromEmail(email: String, callback: (Result<User>) -> Unit) {
        store.collection(users).whereEqualTo(email, email).get().addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("Retrieved user for $email")
                it.result?.documents?.let { documents ->
                    if (documents.isNotEmpty()) {
                        val doc = documents.first()
                        Timber.i("Retrieved user document $doc")
                        callback(Result.Value(User(doc.id,
                                doc.getString(email) ?: "")))
                    } else {
                        Timber.e("No matching user for $email")
                        callback(Result.None)
                    }
                }
            } else {
                Timber.e(it.exception, "Cannot retrieve user for $email")
                callback(Result.None)
            }
        }
    }

    override fun searchUsersByEmail(partialEmail: String, callback: (Result<List<User>>) -> Unit) {
        store.collection(users).whereGreaterThan(email, partialEmail).limit(10).get().addOnCompleteListener {
            if (it.result == null) {
                callback(Result.None)
            } else {
                it.result?.let { result ->
                    val users = mutableListOf<User>()
                    result.documents.forEach { ds ->
                        ds.data?.let { data ->
                            if (data.containsKey(email) && (data[email] as String) != getCurrentUser().email) {
                                users.add(User(ds.id, data[email] as String))
                            }
                        }
                    }
                    callback(Result.Value(users))
                }
            }

        }
    }
}