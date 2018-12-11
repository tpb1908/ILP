package com.tpb.coinz.view.messaging.thread

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.Message
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.users.UserCollection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import utils.DataGenerator

class ThreadViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var vm: ThreadViewModel

    companion object {

        private val chatCollection: ChatCollection = mock()
        private val userCollection: UserCollection = mock()
        private val coinCollection: CoinCollection = mock()
        private val coinBank: CoinBank = mock()
        private val actionObserver: Observer<ThreadViewModel.ThreadAction> = mock()


        private val user = DataGenerator.generateUser()

        private val thread = DataGenerator.generateThread(creator= user)

        private const val messageText = "some test message"
    }
    private lateinit var actionCaptor: KArgumentCaptor<ThreadViewModel.ThreadAction>

    @Before
    fun setUp() {
        actionCaptor = argumentCaptor()
        reset(chatCollection, userCollection, coinCollection, coinBank, actionObserver)
        `when`(userCollection.getCurrentUser()).thenReturn(user)
        `when`(chatCollection.openThreads(any(), any())).thenReturn(mock())
        vm = ThreadViewModel(chatCollection, userCollection, coinCollection, coinBank)
    }

    @Test
    fun openThread() {
        vm.openThread(thread)
        verify(chatCollection, times(1)).openThread(eq(thread), any())
    }

    @Test
    fun postMessageSuccess() {
        vm.openThread(thread)

        val postCallbackCaptor = argumentCaptor<(Result<Unit>) -> Unit>()
        vm.postTextMessage(messageText)

        verify(chatCollection, times(1)).postMessage(any(), postCallbackCaptor.capture())

        vm.actions.observeForever(actionObserver)

        postCallbackCaptor.lastValue.invoke(Result.success(Unit))

        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is ThreadViewModel.ThreadAction.ClearMessageEntry)
    }

    @Test
    fun postMessageFailure() {
        vm.openThread(thread)

        val postCallbackCaptor = argumentCaptor<(Result<Unit>) -> Unit>()
        vm.postTextMessage(messageText)

        verify(chatCollection, times(1)).postMessage(any(), postCallbackCaptor.capture())

        vm.actions.observeForever(actionObserver)

        postCallbackCaptor.lastValue.invoke(Result.failure(Exception()))

        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is ThreadViewModel.ThreadAction.DisplayError)
        assertEquals(R.string.error_posting_message, (actionCaptor.lastValue as ThreadViewModel.ThreadAction.DisplayError).message)

    }

    @Test
    fun transferCoinSuccess() {
        val coin = DataGenerator.generateCoin()
        val callbackCaptor = argumentCaptor<(Result<Message>) -> Unit>()

        vm.openThread(thread)
        vm.transferCoin(coin)
        verify(coinCollection, times(1)).transferCoin(eq(user), eq(thread.otherUser(user)), eq(coin), callbackCaptor.capture())

        val message = DataGenerator.generateMessage(sender=user)
        callbackCaptor.lastValue.invoke(Result.success(message))
        verify(chatCollection, times(1)).postMessage(eq(message), any())
    }

    @Test
    fun transferCoinFailure() {
        val coin = DataGenerator.generateCoin()
        val callbackCaptor = argumentCaptor<(Result<Message>) -> Unit>()

        vm.openThread(thread)
        verify(chatCollection, times(1)).openThread(any(), any())
        vm.transferCoin(coin)
        verify(coinCollection, times(1)).transferCoin(eq(user), eq(thread.otherUser(user)), eq(coin), callbackCaptor.capture())

        vm.actions.observeForever(actionObserver)

        callbackCaptor.lastValue.invoke(Result.failure(Exception()))
        verifyNoMoreInteractions(chatCollection)
        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is ThreadViewModel.ThreadAction.DisplayError)
        assertEquals(R.string.error_sending_coin, (actionCaptor.lastValue as ThreadViewModel.ThreadAction.DisplayError).message)
    }

    @Test
    fun loadCoinsForTransferBeforeBankingComplete() {
        `when`(coinBank.getNumBankable()).thenReturn(10) // anything > 0
        vm.actions.observeForever(actionObserver)

        vm.loadCoinsForTransfer()

        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is ThreadViewModel.ThreadAction.DisplayBankDialog)

    }
}