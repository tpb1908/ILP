package com.tpb.coinz.data.users

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.util.CoinzException
import timber.log.Timber

class FireBaseUserCollection(private val auth: FirebaseAuth, private val store: FirebaseFirestore) : UserCollection {

    private val users = "users"
    private val email = "email"

    override fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun createUser(id: String, email: String, callback: (Result<User>) -> Unit) {
        store.collection(users).document(id).set(
                mapOf("email" to email)
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(Result.success(getCurrentUser()))
            } else {

            }
        }
    }

    override fun getCurrentUser(): User {
        if (!isSignedIn()) throw Exception("User not signed in")
        val firebaseUser = auth.currentUser
        return User(firebaseUser!!.uid, firebaseUser.email!!)
    }

    override fun retrieveUsers(callback: (Result<List<User>>) -> Unit) {
        store.collection(users).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val users = mutableListOf<User>()
                it.result?.documents?.forEach { doc ->
                    users.add(User(doc.id, doc.getString(email) ?: ""))
                }
                callback(Result.success(users))
            }
        }
    }

    override fun retrieveUserFromEmail(email: String, callback: (Result<User>) -> Unit) {
        store.collection(users).whereEqualTo("email", email).get().addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("Retrieved user for $email")
                it.result?.documents?.let { documents ->
                    if (documents.isNotEmpty()) {
                        val doc = documents.first()
                        Timber.i("Retrieved user document $doc")
                        callback(Result.success(User(doc.id,
                                doc.getString("email") ?: "")))
                    } else {
                        Timber.e("No matching user for $email")
                        callback(Result.failure(CoinzException.NotFoundException()))
                    }
                }
            } else {
                Timber.e(it.exception, "Cannot retrieve user for $email")
                callback(Result.failure(CoinzException.UnknownException()))
            }
        }
    }

    override fun searchUsersByEmail(partialEmail: String, callback: (Result<List<User>>) -> Unit) {
        store.collection(users).whereGreaterThan(email, partialEmail).limit(10).get().addOnCompleteListener {
            if (it.result == null) {
                callback(Result.failure(CoinzException.NotFoundException()))
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
                    callback(Result.success(users))
                }
            }
        }
    }

}