package com.dashdevs.dashdevscomposesample

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Model
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.observe
import androidx.ui.core.*
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.DefaultAlpha
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import coil.Coil
import coil.request.GetRequest
import coil.request.LoadRequest
import com.dashdevs.dashdevscomposesample.data.Transaction

class MainActivity : AppCompatActivity() {
    val viewModel: TransactionsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Coil.imageLoader(this).clearMemory()
        val transactionsList = TransactionsList(emptyList())
        viewModel.transactions.observe(this) {
            transactionsList.transactions = it
        }
        viewModel.fetchTransactions()
        setContent {
            MaterialTheme {
                drawTransactions(transactionsList = transactionsList)
            }
        }
    }
}

@Model
data class TransactionsList(var transactions: List<Transaction>)

@Composable
fun drawTransactions(transactionsList: TransactionsList) {
    AdapterList(data = transactionsList.transactions) {
        TransactionRow(transaction = it)
    }
}

@Composable
fun drawSomeState(someState: SomeState) {

    Text(text = "Counter = ${someState.count}")
}

@Composable
fun TransactionRow(transaction: Transaction) {
    Row(
        modifier = Modifier
            .preferredHeight(56.dp)
            .ripple()
            .fillMaxWidth()
    ) {
        ImageByUrl(
            url = transaction.iconUrl,
            modifier = Modifier
                .gravity(Alignment.CenterVertically)
                .padding(start = 16.dp, end = 16.dp)
                .preferredSize(24.dp, 24.dp)
        )
        Column(
            modifier = Modifier
                .weight(1.0f, true)
                .gravity(Alignment.CenterVertically)
                .padding(end = 8.dp)
        ) {
            Text(text = transaction.amount)
            Box(
                modifier = Modifier
                    .preferredHeight(8.dp)
            )
            Text(text = "Line 2")
        }
        Text(
            text = "Amount",
            modifier = Modifier.wrapContentWidth(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )
    }
}

@Model
class SomeState(var count: Int = 0)

@Model
class ImageUiState(var state: UiState)

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: ImageAsset?) : UiState()
}

private suspend fun loadToImageAsset(context: Context, url: String): UiState {
    val request = GetRequest.Builder(context)
        .data(url)
        .build()
    return UiState.Success(
        Coil.imageLoader(context)
            .execute(request).drawable?.toBitmap()?.asImageAsset()
    )
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}

@Composable
fun ImageByUrl(
    url: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Inside,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    onError: @Composable() () -> Unit = {},
    onLoading: @Composable() () -> Unit = {}
) {
    val imageState = ImageUiState(UiState.Loading)
    val request = LoadRequest.Builder(ContextAmbient.current)
        .data(url)
        .target(
            onError = {
                imageState.state = UiState.Success(null)
            },
            onSuccess = {
                imageState.state = UiState.Success(it.toBitmap().asImageAsset())
            }
        )
        .build()
    Coil.imageLoader(ContextAmbient.current).execute(request)

    drawImageState(
        uiState = imageState,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        onError = onError,
        onLoading = onLoading
    )
}

@Composable
fun drawImageState(
    uiState: ImageUiState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Inside,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    onError: @Composable() () -> Unit = { Text(text = "Error") },
    onLoading: @Composable() () -> Unit = { Text(text = "Loading...") }
) {
    when (val state = uiState.state) {
        UiState.Loading -> onLoading()
        is UiState.Success -> state.data?.let {
            Image(
                asset = it,
                modifier = modifier,
                alignment = alignment,
                contentScale = contentScale,
                alpha = alpha,
                colorFilter = colorFilter
            )
        } ?: run {
            onError()
        }
    }
}