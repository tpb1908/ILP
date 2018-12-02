package com.tpb.coinz.data.util

/**
 * Represents a listener which can be removed
 * Allows the listener to be removed without needing a reference to the creator of the listener
 */
abstract class Registration {

    /**
     * Removes the listener which the registration refers to
     */
    abstract fun deregister()

}