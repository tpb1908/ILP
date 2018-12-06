package com.tpb.coinz.data.util

/**
 * Registration representing multiple other registrations, which are all deregistered when the [CompositeRegistration]
 * is deregistered
 */
class CompositeRegistration constructor(private val registrations: MutableList<Registration> = mutableListOf()) : Registration() {


    fun add(registration: Registration) = registrations.add(registration)

    override fun deregister() {
        registrations.forEach(Registration::deregister)
    }
}