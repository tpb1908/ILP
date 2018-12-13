package com.tpb.coinz.view.bank

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.scoreboard.Scoreboard
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.users.UserCollection
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import utils.DataGenerator

class BankViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var vm: BankViewModel

    companion object {
        private val mockCoinBank: CoinBank = mock()
        private val mockUserCollection: UserCollection = mock()
        private val mockMapStore: MapStore = mock()
        private val mockScoreboard: Scoreboard = mock()
        private val actionObserver = mock<Observer<BankViewModel.BankAction>>()
        private val loadingStateObserver = mock<Observer<Boolean>>()
        //TODO this looks disgusting and isn't very clear, I should probably move the view info into an explicit class
        private val bankableObserver = mock<Observer<Pair<List<SelectableItem<Coin>>, List<SelectableItem<Coin>>>>>()
        private val testUser = User("uid", "test@test.com")

    }

    private lateinit var actionCaptor: KArgumentCaptor<BankViewModel.BankAction>
    private lateinit var loadingStateCaptor: KArgumentCaptor<Boolean>
    private lateinit var bankableCaptor: KArgumentCaptor<Pair<List<SelectableItem<Coin>>, List<SelectableItem<Coin>>>>
    private lateinit var bankableCoinsCallbackCaptor: KArgumentCaptor<(Result<List<Coin>>) -> Unit>

    @Before
    fun setUp() {
        actionCaptor = argumentCaptor()
        loadingStateCaptor = argumentCaptor()
        bankableCaptor = argumentCaptor()
        bankableCoinsCallbackCaptor = argumentCaptor()
        reset(mockCoinBank, mockUserCollection, mockMapStore, actionObserver, loadingStateObserver, bankableObserver)
        `when`(mockUserCollection.getCurrentUser()).thenAnswer {
            println("Returning test user")
            testUser
        }
        `when`(mockCoinBank.getBankableCoins(any(), bankableCoinsCallbackCaptor.capture())).thenReturn(mock())
        vm = BankViewModel(mockCoinBank, mockUserCollection, mockMapStore, mockScoreboard)
        vm.loadingState.observeForever(loadingStateObserver)
        vm.actions.observeForever(actionObserver)
    }

    /**
     * Test that the BankViewModel starts loading banked coins and posts a SetLoadingState action when bound
     */
    @Test
    fun getBankableCoinsLoadingState() {
        vm.bind()
        verify(loadingStateObserver, times(1)).onChanged(loadingStateCaptor.capture())
        assertTrue("Loading state should be true", loadingStateCaptor.lastValue)
    }

    /**
     * Test that the BankViewModel responds to the return of the banked coins call by splitting the received and
     * non-received coins, and posting these coins
     */
    @Test
    fun getBankableCoinsSuccess() {

        vm.bind()
        vm.bankableCoins.observeForever(bankableObserver)
        val coins = (1..10).map { DataGenerator.generateCoin(received = it > 5) }
        bankableCoinsCallbackCaptor.lastValue.invoke(Result.success(coins))


        verify(bankableObserver, times(1)).onChanged(bankableCaptor.capture())
        assertTrue("First list should be collected coins", bankableCaptor.lastValue.first.all { !it.item.received })
        assertTrue("Second list should be received coins", bankableCaptor.lastValue.second.all { it.item.received })
        assertTrue("The same coins should be present", coins.all { coin ->
            bankableCaptor.lastValue.first.any { it.item == coin } || bankableCaptor.lastValue.second.any { it.item == coin }
        })
    }

    /**
     * Test that the BankViewModel posts a DisplayError action when the [CoinBank.getBankableCoins] call
     * returns a failure
     */
    @Test
    fun getBankableCoinsFailure() {
        vm.bind()

        vm.bankableCoins.observeForever(bankableObserver)

        verify(bankableObserver, times(0)).onChanged(com.nhaarman.mockitokotlin2.any())

        bankableCoinsCallbackCaptor.lastValue.invoke(Result.failure(Exception()))
        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is BankViewModel.BankAction.DisplayError)
        verify(loadingStateObserver, times(2)).onChanged(loadingStateCaptor.capture())
        assertTrue(loadingStateCaptor.firstValue)
        assertFalse(loadingStateCaptor.secondValue)
    }

    /**
     * Test that the retry callback in the [BankViewModel.BankAction.DisplayError] action attempts to retry the loading
     * of bankable coins
     */
    @Test
    fun testRetryOnFailedLoad() {
        vm.bind()
        bankableCoinsCallbackCaptor.lastValue.invoke(Result.failure(Exception()))
        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is BankViewModel.BankAction.DisplayError)

        reset(actionObserver)
        val callback = (actionCaptor.lastValue as BankViewModel.BankAction.DisplayError).retry
        callback.invoke()
        verify(mockCoinBank, times(2)).getBankableCoins(any(), any())
    }

    @Test
    fun bankCoins() {
        vm.bind()
        val coins = (1..40).map { DataGenerator.generateCoin(received = true) }
        bankableCoinsCallbackCaptor.lastValue.invoke(Result.success(coins))
        vm.bankableCoins.observeForever(bankableObserver)
        verify(bankableObserver, times(1)).onChanged(bankableCaptor.capture())
        val selectable = bankableCaptor.lastValue.second
        val selected = selectable.subList(0, 10)
        selected.forEach {
            assertTrue(vm.attemptSelect(it))
        }

        val map = DataGenerator.generateMap()
        `when`(mockMapStore.getLatest(any())).thenAnswer {
            (it.arguments[0] as ((Result<Map>) -> Unit)).invoke(Result.success(map))
        }
        vm.bankCoins()



        val bankCaptor = argumentCaptor<((Result<List<Coin>>) -> Unit)>()
        verify(mockCoinBank, times(1)).bankCoins(eq(testUser), eq(selected.map { it.item }), eq(map.rates), bankCaptor.capture())
        bankCaptor.lastValue.invoke(Result.success(coins))

        verify(bankableObserver, times(2)).onChanged(bankableCaptor.capture())
        //TODO
//        val bankable = bankableCaptor.lastValue.second
//        assertTrue("Bankable coins shouldn't contain any of the banked coins",
//                bankable.containsAll(selectable-selected))
    }

    /**
     * Test that the [BankViewModel] allows selection of no more than [CoinBank.getNumBankable] received coins, and
     * rejects any further calls.
     * Test that it also allows selection of received coins regardless of this limit
     * Test that deselecting the selected received coins reduces the selected count
     */
    @Test
    fun testSelectionFlow() {
        val coins = (1..40).map { DataGenerator.generateCoin(received = it > 30) }
        `when`(mockCoinBank.getNumBankable()).thenReturn(25)
        vm.bind()
        bankableCoinsCallbackCaptor.lastValue.invoke(Result.success(coins))

        val collectedSelectionObserver = mock<Observer<Int>>()
        val selectedCaptor = argumentCaptor<Int>()
        vm.numCollectedSelected.observeForever(collectedSelectionObserver)

        // Attempt to select 25 user-collected coins which are not currently selected
        coins.subList(0, 25).forEach {
            assertTrue(vm.attemptSelect(SelectableItem(false, it)))
        }
        // All 25 coins should be selected
        verify(collectedSelectionObserver, times(25)).onChanged(selectedCaptor.capture())

        // No more than 25 user-collected should be selected
        assertFalse(vm.attemptSelect(SelectableItem(false, coins[26])))
        verifyNoMoreInteractions(collectedSelectionObserver)
        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is BankViewModel.BankAction.SelectionFull)

        // Received coins should be selectable
        val item = SelectableItem(false, coins.last())
        assertTrue(vm.attemptSelect(item))
        verifyNoMoreInteractions(collectedSelectionObserver)
        verifyNoMoreInteractions(actionObserver)
        assertTrue(item.selected)

        // Test removal of items
        reset(collectedSelectionObserver)

        coins.subList(0, 25).forEach {
            vm.deselect(SelectableItem(true, it))
        }
        verify(collectedSelectionObserver, times(25)).onChanged(selectedCaptor.capture())
    }

}