package com.tpb.coinz.data.coin

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.users.User

abstract class FireStoreCoinManager(protected val store: FirebaseFirestore) {

    protected val collected = "collected"
    protected val coins = "coins"
    protected val banked = "banked"

    protected inline fun coins(user: User): CollectionReference = store.collection(collected).document(user.uid).collection(coins)
    protected inline fun banked(user: User): CollectionReference = store.collection(collected).document(user.uid).collection(banked)



}