package com.dashdevs.dashdevscomposesample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashdevs.dashdevscomposesample.data.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class TransactionsViewModel : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions
    private val icons = arrayOf(
        "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Circle-icons-computer.svg/1200px-Circle-icons-computer.svg.png",
        "https://st2.depositphotos.com/5266903/12091/v/950/depositphotos_120913814-stock-illustration-price-tag-flat-vector-icon.jpg",
        "https://cdn.iconscout.com/icon/free/png-512/overwatch-2-569226.png",
        "https://www.seguetech.com/wp-content/uploads/2016/04/Twitter.png"
    )

    private fun getRandomUrl(): String? {
        val index = Random.nextInt(5)
        return if (index >= icons.size) null
        else icons[index]
    }

    fun fetchTransactions(page: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(550)
            val transactionsList = (0..19).map {
                Transaction(
                    id = (it + 10 * page).toString(),
                    amount = "BHD ${(it + 10 * page)}.000",
                    title = "Some title",
                    subtitle = "Some bank name with iban bla bla bla ****",
                    iconUrl = getRandomUrl(),
                    isDebit = Random.nextBoolean()
                )
            }
            _transactions.postValue(transactionsList)
        }
    }
}