package com.tpb.coinz.view.bank

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.data.util.Registration
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import utils.DataGenerator
import utils.MockitoUtils.anyOfType
import utils.MockitoUtils.argumentCaptor
import utils.MockitoUtils.typedAny

class BankViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var vm: BankViewModel

    companion object {
        private val mockCoinBank = Mockito.mock(CoinBank::class.java)
        private val mockUserCollection = Mockito.mock(UserCollection::class.java)
        private val mockMapStore = Mockito.mock(MapStore::class.java)

        private val testUser = User("uid", "test@test.com")

        @BeforeClass @JvmStatic
        fun beforeClass() {
            println("Running beforeClass")
            `when`(mockUserCollection.getCurrentUser()).thenAnswer {
                println("Returning test user")
                testUser
            }

        }
    }

    @Before
    fun setUp() {
        vm = BankViewModel(mockCoinBank, mockUserCollection, mockMapStore)
        reset(mockCoinBank)
    }

    /**
     * Test that the BankViewModel starts loading banked coins and posts a SetLoadingState action when bound
     */
    @Test
    fun getBankableCoinsLoadingState() {
        val actionObserver = anyOfType<Observer<BankViewModel.BankAction>>()
        val actionCaptor = argumentCaptor<BankViewModel.BankAction>()
        `when`(mockCoinBank.getBankableCoins(typedAny(), typedAny())).thenReturn(Mockito.mock(Registration::class.java))
        vm.actions.observeForever(actionObserver)
        vm.bind()
        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.value is BankViewModel.BankAction.SetLoadingState)
        assertTrue((actionCaptor.value as BankViewModel.BankAction.SetLoadingState).loading)
    }

    /**
     * Test that the BankViewModel responds to the return of the banked coins call by splitting the received and
     * non-received coins, and posting these coins
     */
    @Test
    fun getBankableCoinsSuccess() {
        var listener: ((Result<List<Coin>>) -> Unit)? = null
        `when`(mockCoinBank.getBankableCoins(typedAny(), typedAny())).thenAnswer {
            listener = it.getArgument(1)
            println("Returning mocked registration")
            Mockito.mock(Registration::class.java)
        }
        vm.bind()
        assertNotNull(listener)
        val bankableObserver = anyOfType<Observer<Pair<List<SelectableItem<Coin>>, List<SelectableItem<Coin>>>>>()
        vm.bankableCoins.observeForever(bankableObserver)
        val coins = (1..10).map { DataGenerator.generateCoin(received = it > 5) }
        listener?.invoke(Result.success(coins))

        val bankableCaptor = argumentCaptor<Pair<List<SelectableItem<Coin>>, List<SelectableItem<Coin>>>>()
        verify(bankableObserver, times(1)).onChanged(bankableCaptor.capture())
        assertTrue("First list should be collected coins", bankableCaptor.value.first.all { !it.item.received })
        assertTrue("Second list should be received coins", bankableCaptor.value.second.all { it.item.received })
        assertTrue("The same coins should be present", coins.all { coin ->
            bankableCaptor.value.first.any { it.item == coin } || bankableCaptor.value.second.any { it.item == coin }
        })
    }

    /**
     * Test that the BankViewModel posts a DisplayError action when the [CoinBank.getBankableCoins] call
     * returns a failure
     */
    @Test
    fun getBankableCoinsFailure() {
        var listener: ((Result<List<Coin>>) -> Unit)? = null
        `when`(mockCoinBank.getBankableCoins(typedAny(), typedAny())).thenAnswer {
            listener = it.getArgument(1)
            println("Returning mocked registration")
            Mockito.mock(Registration::class.java)
        }
        vm.bind()
        assertNotNull(listener)
        val bankableObserver = anyOfType<Observer<Pair<List<SelectableItem<Coin>>, List<SelectableItem<Coin>>>>>()
        vm.bankableCoins.observeForever(bankableObserver)

        val actionObserver = anyOfType<Observer<BankViewModel.BankAction>>()
        val actionCaptor = argumentCaptor<BankViewModel.BankAction>()

        verify(bankableObserver, times(0)).onChanged(typedAny())

        vm.actions.observeForever(actionObserver)
        listener?.invoke(Result.failure(Exception()))
        verify(actionObserver, times(3)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.allValues[0] is BankViewModel.BankAction.SetLoadingState)
        assertTrue(actionCaptor.allValues[1] is BankViewModel.BankAction.DisplayError)
        assertTrue(actionCaptor.allValues[2] is BankViewModel.BankAction.SetLoadingState)
    }

    /**
     * Test that the retry callback in the [BankViewModel.BankAction.DisplayError] action attempts to retry the loading
     * of bankable coins
     */
    @Test
    fun testRetryOnFailedLoad() {
        var listener: ((Result<List<Coin>>) -> Unit)? = null
        `when`(mockCoinBank.getBankableCoins(typedAny(), typedAny())).thenAnswer {
            listener = it.getArgument(1)
            println("Returning mocked registration")
            Mockito.mock(Registration::class.java)
        }
        vm.bind()
        assertNotNull(listener)
        val actionObserver = anyOfType<Observer<BankViewModel.BankAction>>()
        val actionCaptor = argumentCaptor<BankViewModel.BankAction>()
        vm.actions.observeForever(actionObserver)
        listener?.invoke(Result.failure(Exception()))
        verify(actionObserver, times(3)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.allValues[1] is BankViewModel.BankAction.DisplayError)

        reset(actionObserver)
        val callback = (actionCaptor.allValues[1] as BankViewModel.BankAction.DisplayError).retry
        callback.invoke()
        verify(mockCoinBank, times(2)).getBankableCoins(typedAny(), typedAny())
        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
    }

    @Test
    fun getNumStillBankable() {
    }


    @Test
    fun bankCoins() {
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
        var listener: ((Result<List<Coin>>) -> Unit)? = null
        `when`(mockCoinBank.getBankableCoins(typedAny(), typedAny())).thenAnswer {
            listener = it.getArgument(1)
            println("Returning mocked registration")
            Mockito.mock(Registration::class.java)
        }
        `when`(mockCoinBank.getNumBankable()).thenReturn(25)
        vm.bind()
        assertNotNull(listener)
        listener?.invoke(Result.success(coins))

        val collectedSelectionObserver = anyOfType<Observer<Int>>()
        val selectedCaptor = argumentCaptor<Int>()
        vm.numCollectedSelected.observeForever(collectedSelectionObserver)

        val actionsObserver = anyOfType<Observer<BankViewModel.BankAction>>()
        val actionsCaptor = argumentCaptor<BankViewModel.BankAction>()
        vm.actions.observeForever(actionsObserver)

        // Attempt to select 25 user-collected coins which are not currently selected
        coins.subList(0, 25).forEach {
            assertTrue(vm.attemptSelect(SelectableItem(false, it)))
        }
        // All 25 coins should be selected
        verify(collectedSelectionObserver, times(25)).onChanged(selectedCaptor.capture())

        // No more than 25 user-collected should be selected
        assertFalse(vm.attemptSelect(SelectableItem(false, coins[26])))
        verifyNoMoreInteractions(collectedSelectionObserver)
        verify(actionsObserver, times(2)).onChanged(actionsCaptor.capture())
        assertTrue(actionsCaptor.value is BankViewModel.BankAction.SelectionFull)

        // Received coins should be selectable
        val item = SelectableItem(false, coins.last())
        assertTrue(vm.attemptSelect(item))
        verifyNoMoreInteractions(collectedSelectionObserver)
        verifyNoMoreInteractions(actionsObserver)
        assertTrue(item.selected)

        // Test removal of items
        reset(collectedSelectionObserver)

        coins.subList(0, 25).forEach {
            vm.deselect(SelectableItem(true, it))
        }
        verify(collectedSelectionObserver, times(25)).onChanged(selectedCaptor.capture())
    }



}