package com.tpb.coinz.data.users

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid: String, val email: String) : Parcelable