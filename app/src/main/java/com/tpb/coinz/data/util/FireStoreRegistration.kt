package com.tpb.coinz.data.util

import com.google.firebase.firestore.ListenerRegistration

/**
 * Registration wrapping a FireBase [ListenerRegistration]
 */
class FireStoreRegistration(private val reg: ListenerRegistration?) : Registration() {

    override fun deregister() {
        reg?.remove()
    }
}