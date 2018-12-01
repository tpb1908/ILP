package com.tpb.coinz.data.chat

import com.tpb.coinz.Result
import com.tpb.coinz.data.users.User

/**
 * Interface for managing the threads between a user and other users
 *
 */
interface ChatCollection {

    /**
     * Create a thread from [creator] to [partner] and callback once created
     *
     */
    fun createThread(creator: User, partner: User, callback: (Result<Thread>) -> Unit)

    /**
     * Add a listener for changes to a particular thread
     * The thread is now open for posting to via [postMessage]
     * @param thread The thread to listen for changes on
     * @param listener A listener which is passed the current message list on each update
     */
    fun openThread(thread: Thread, listener: (Result<List<Message>>) -> Unit)

    /**
     * Remove the listener for a particular thread
     * @param thread The thread to remove listeners for
     */
    fun closeThread(thread: Thread)

    /**
     * Post a message to the currently open thread
     * @param message The message to post from the thread creator to the thread partner
     * @param callback Result callback for success of sending the message
     */
    fun postMessage(message: Message, callback: (Result<Boolean>) -> Unit)

    /**
     * Add a listener for changes to the threads for the current user
     * @param user The user to load threads for. Both those which the user created, and those where they are the recipient
     * @param listener A listener which is passed the current list of user threads on change
     */
    fun openThreads(user: User, listener: (Result<List<Thread>>) -> Unit)

    /**
     * Close the thread listener added by [openThreads]
     */
    fun closeThreads()
}