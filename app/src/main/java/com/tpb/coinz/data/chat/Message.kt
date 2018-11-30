package com.tpb.coinz.data.chat

import com.tpb.coinz.data.users.User

data class Message(val timestamp: Long, val sender: User, val message: String)