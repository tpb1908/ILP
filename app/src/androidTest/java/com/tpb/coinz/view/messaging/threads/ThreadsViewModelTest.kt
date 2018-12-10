package com.tpb.coinz.view.messaging.threads

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.users.UserCollection
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.mockito.Mockito.`when`
import utils.DataGenerator

class ThreadsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var vm: ThreadsViewModel

    companion object {

        private val chatCollection: ChatCollection = mock()
        private val userCollection: UserCollection = mock()
        private val actionObserver: Observer<ThreadsViewModel.ThreadsAction> = mock()
        private val loadingStateObserver: Observer<Boolean> = mock()

        private val threadsObserver: Observer<List<Thread>> = mock()

        private val user = DataGenerator.generateUser()

        private val threads = (1..10).map { DataGenerator.generateThread() }

    }
    private lateinit var actionCaptor: KArgumentCaptor<ThreadsViewModel.ThreadsAction>
    private lateinit var threadsCaptor: KArgumentCaptor<List<Thread>>

    private lateinit var loadingStateCaptor: KArgumentCaptor<Boolean>

    @Before
    fun setUp() {
        actionCaptor = argumentCaptor()
        threadsCaptor = argumentCaptor()
        loadingStateCaptor = argumentCaptor()
        reset(chatCollection, userCollection, actionObserver, threadsObserver, loadingStateObserver)
        `when`(userCollection.getCurrentUser()).thenReturn(user)
        `when`(chatCollection.openThreads(any(), any())).thenReturn(mock())
        vm = ThreadsViewModel(chatCollection, userCollection)
        vm.actions.observeForever(actionObserver)
        vm.loadingState.observeForever(loadingStateObserver)
    }

    @Test
    fun testThreadLoadingSuccess() {
        vm.bind()

        val callbackCaptor = argumentCaptor<(Result<List<Thread>>) -> Unit>()

        verify(chatCollection, times(1)).openThreads(eq(user), callbackCaptor.capture())

        verify(loadingStateObserver, times(1)).onChanged(loadingStateCaptor.capture())
        assertTrue("ViewModel should post loading state", loadingStateCaptor.lastValue)

        vm.threads.observeForever(threadsObserver)

        // Invoke the callback with a successful result
        callbackCaptor.lastValue.invoke(Result.success(threads))

        // Verify that the threads in the Result are posted to the threads LiveData
        verify(threadsObserver, times(1)).onChanged(threadsCaptor.capture())
        assertTrue("ViewModel should post same threads as ChatCollection", threads == threadsCaptor.lastValue)
        // Verify that the loading state is set to false
        verify(loadingStateObserver, times(2)).onChanged(loadingStateCaptor.capture())
        assertFalse("ViewModel should post false loading state", loadingStateCaptor.lastValue)
    }

    @Test
    fun testThreadLoadingFailure() {
        vm.bind()

        val callbackCaptor = argumentCaptor<(Result<List<Thread>>) -> Unit>()

        verify(chatCollection, times(1)).openThreads(eq(user), callbackCaptor.capture())

        verify(loadingStateObserver, times(1)).onChanged(loadingStateCaptor.capture())
        assertTrue("ViewModel should post loading state", loadingStateCaptor.lastValue)

        vm.threads.observeForever(threadsObserver)

        // Invoke the callback with a failure result
        callbackCaptor.lastValue.invoke(Result.failure(Exception()))

        // Verify that no threads are posted and DisplayError action is posted
        verify(threadsObserver, times(0)).onChanged(any())
        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is ThreadsViewModel.ThreadsAction.DisplayError)

        // Invoke the retry callback and check that another call to openThreads is made
        val retry = (actionCaptor.lastValue as ThreadsViewModel.ThreadsAction.DisplayError).retry
        retry.invoke()
        verify(chatCollection, times(2)).openThreads(eq(user), callbackCaptor.capture())

    }

    @Test
    fun createChatSuccess() {
        val partner = DataGenerator.generateUser()
        val userCallbackCaptor = argumentCaptor<(Result<User>) -> Unit>()

        vm.createChat(partner.email)
        verify(userCollection, times(1)).retrieveUserFromEmail(eq(partner.email), userCallbackCaptor.capture())

        val threadCallbackCaptor = argumentCaptor<(Result<Thread>) -> Unit>()

        userCallbackCaptor.lastValue.invoke(Result.success(partner))

        verify(chatCollection, times(1)).createThread(eq(user), eq(partner), threadCallbackCaptor.capture())

        val threadIntentObserver = mock<Observer<Thread>>()
        val threadIntentCaptor = argumentCaptor<Thread>()
        vm.threads.observeForever(threadsObserver)
        vm.threadIntents.observeForever(threadIntentObserver)

        val thread = DataGenerator.generateThread(creator = user, partner = partner)
        threadCallbackCaptor.lastValue.invoke(Result.success(thread))

        verify(loadingStateObserver, times(2)).onChanged(loadingStateCaptor.capture())

        assertTrue("ViewModel should post loading state", loadingStateCaptor.firstValue)
        assertFalse("ViewModel should post false loading state", loadingStateCaptor.secondValue)

        verify(threadsObserver, times(1)).onChanged(threadsCaptor.capture())
        assertEquals(thread, threadsCaptor.lastValue.last())
        verify(threadIntentObserver, times(1)).onChanged(threadIntentCaptor.capture())
        assertEquals(thread, threadIntentCaptor.lastValue)

    }

    @Test
    fun createChatUserSearchFailure() {

        val partner = DataGenerator.generateUser()
        val userCallbackCaptor = argumentCaptor<(Result<User>) -> Unit>()

        vm.createChat(partner.email)
        verify(userCollection, times(1)).retrieveUserFromEmail(eq(partner.email), userCallbackCaptor.capture())

        userCallbackCaptor.lastValue.invoke(Result.failure(Exception()))

        verifyNoMoreInteractions(chatCollection)

        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is ThreadsViewModel.ThreadsAction.DisplayError)
        assertEquals(R.string.error_loading_user, (actionCaptor.lastValue as ThreadsViewModel.ThreadsAction.DisplayError).message)

    }

    @Test
    fun createChatThreadFailure() {

        val partner = DataGenerator.generateUser()
        val userCallbackCaptor = argumentCaptor<(Result<User>) -> Unit>()

        vm.createChat(partner.email)
        verify(userCollection, times(1)).retrieveUserFromEmail(eq(partner.email), userCallbackCaptor.capture())

        val threadCallbackCaptor = argumentCaptor<(Result<Thread>) -> Unit>()

        userCallbackCaptor.lastValue.invoke(Result.success(partner))

        verify(chatCollection, times(1)).createThread(eq(user), eq(partner), threadCallbackCaptor.capture())

        val threadIntentObserver = mock<Observer<Thread>>()
        vm.threads.observeForever(threadsObserver)
        vm.threadIntents.observeForever(threadIntentObserver)

        threadCallbackCaptor.lastValue.invoke(Result.failure(java.lang.Exception()))

        verify(actionObserver, atLeastOnce()).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is ThreadsViewModel.ThreadsAction.DisplayError)
        assertEquals(R.string.error_creating_thread, (actionCaptor.lastValue as ThreadsViewModel.ThreadsAction.DisplayError).message)

    }

    @Test
    fun createChatWithSelf() {

        vm.createChat(user.email)

        verify(loadingStateObserver, times(2)).onChanged(loadingStateCaptor.capture())
        assertTrue("ViewModel should post true loading state", loadingStateCaptor.firstValue)
        assertFalse("ViewModel should post false loading state", loadingStateCaptor.secondValue)

        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is ThreadsViewModel.ThreadsAction.DisplayMessage)
        assertEquals((actionCaptor.lastValue as ThreadsViewModel.ThreadsAction.DisplayMessage).message, R.string.error_thread_to_self)


    }

}