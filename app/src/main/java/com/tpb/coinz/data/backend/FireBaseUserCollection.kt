package com.tpb.coinz.data.backend

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.Result
import com.tpb.coinz.db.users
import timber.log.Timber

class FireBaseUserCollection(private val store: FirebaseFirestore) : UserCollection {

    override fun getCurrentUser(): UserCollection.User {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return UserCollection.User(firebaseUser?.uid ?: "uid_error", firebaseUser?.email ?: "email_error")
    }

    override fun retrieveUsers(callback: (Result<List<UserCollection.User>>) -> Unit) {
        store.collection(users).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val users = mutableListOf<UserCollection.User>()
                it.result?.documents?.forEach { doc ->
                    users.add(UserCollection.User(doc.id, doc.getString("email") ?: ""))
                }
                callback(Result.Value(users))
            }
        }
    }

    override fun retrieveUserFromEmail(email: String, callback: (Result<UserCollection.User>) -> Unit) {
        store.collection(users).whereEqualTo("email", email).get().addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("Retrieved user for $email")
                it.result?.documents?.let {documents ->
                    if (documents.isNotEmpty()) {
                        val doc = documents.first()
                        Timber.i("Retrieved user document $doc")
                        callback(Result.Value(UserCollection.User(doc.id,
                                doc.getString("email") ?: "")))
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
}