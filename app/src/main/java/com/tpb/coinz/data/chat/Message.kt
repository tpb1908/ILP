package com.tpb.coinz.data.chat

import com.tpb.coinz.data.users.User

/**
 * Data class for messages sent through [ChatCollection]
 * @param timestamp Timestamp at which the message was sent
 * @param sender The user who sent the message
 * @param message The message body
 */
data class Message(val timestamp: Long, val sender: User, val message: String)