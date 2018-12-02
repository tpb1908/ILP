package com.tpb.coinz.data.util

/**
 * Registration representing multiple other registrations, which are all deregistered when the [CompositeRegistration]
 * is deregistered
 */
class CompositeRegistration constructor(private val registrations: MutableList<Registration> = mutableListOf()): Registration() {

    constructor(vararg registrations: Registration) : this(registrations.toMutableList())

    constructor(registrations: Collection<Registration>) : this(registrations.toMutableList())

    fun add(registration: Registration) = registrations.add(registration)

    override fun deregister() {
        registrations.forEach(Registration::deregister)
    }
}