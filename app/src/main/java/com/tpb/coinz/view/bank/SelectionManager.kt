package com.tpb.coinz.view.bank

interface SelectionManager<T> {

    fun attemptSelect(item: SelectableItem<T>): Boolean

    fun deselect(item: SelectableItem<T>)

}