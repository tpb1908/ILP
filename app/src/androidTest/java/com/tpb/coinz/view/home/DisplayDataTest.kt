package com.tpb.coinz.view.home

import androidx.lifecycle.MutableLiveData
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.tpb.coinz.*
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Transaction
import com.tpb.coinz.view.base.ActionLiveData
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.declareMock
import utils.DataGenerator

@LargeTest
@RunWith(AndroidJUnit4::class)
class DisplayDataTest : KoinTest {


    lateinit var vm: HomeViewModel
    lateinit var bankInfo: MutableLiveData<BankInfo>
    lateinit var recentlyBanked: MutableLiveData<List<Transaction>>
    lateinit var coins: MutableLiveData<List<Coin>>
    lateinit var collectionInfo: MutableLiveData<MapInfo>
    lateinit var actions: ActionLiveData<HomeViewModel.HomeAction>
    lateinit var user: MutableLiveData<com.tpb.coinz.data.users.User>
    lateinit var threads: MutableLiveData<List<Thread>>

    val testUser = DataGenerator.generateUser()

    @Rule
    @JvmField
    val rule = ActivityTestRule<HomeActivity>(HomeActivity::class.java)

    @Before
    fun setUp() {
        bankInfo = MutableLiveData()
        recentlyBanked = MutableLiveData()
        coins = MutableLiveData()
        collectionInfo = MutableLiveData()
        actions = ActionLiveData()
        user = MutableLiveData()
        threads = MutableLiveData()

        vm = declareMock<HomeViewModel>()
        whenever(vm.bankInfo).thenReturn(bankInfo)
        whenever(vm.recentlyBanked).thenReturn(recentlyBanked)
        whenever(vm.coins).thenReturn(coins)
        whenever(vm.collectionInfo).thenReturn(collectionInfo)
        whenever(vm.actions).thenReturn(actions)
        whenever(vm.user).thenReturn(user)
        whenever(vm.threads).thenReturn(threads)

//        loadKoinModules(listOf(viewModelModule, module {
//            viewModel(override = true) { vm }
//        }))
        println("Mock is $vm")
        println("Return of mocked vm value ${vm.user}")

    }

    @Test
    fun testUserDisplay() {
        user.postValue(testUser)
    }

    @Test
    fun testCollectionInfoDisplay() {

    }

    @Test
    fun testThreadsDisplay() {

    }

    @Test
    fun testRecentlyBankedDisplay() {

    }

    @Test
    fun testBankInfoDisplay() {

    }

    @After
    fun cleanUp() {
        stopKoin()
    }

}