package com.dashdevs.dashdevscomposesample

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Model
import androidx.core.graphics.drawable.toBitmap
import androidx.ui.core.*
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.ColorFilter
import androidx.ui.graphics.DefaultAlpha
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import coil.Coil
import coil.request.GetRequest
import coil.request.LoadRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Coil.imageLoader(this).clearMemory()
        setContent {
            MaterialTheme {

                val someState = SomeState()

                VerticalScroller {
                    Column {
                        Row {
                            drawSomeState(someState = someState)
                        }
                        Row {
                            Column {
                                Button(text = { Text(text = "Increase") }, onClick = {
                                    someState.count++
                                })
                            }
                        }
                        Row {
                            ImageByUrl(url = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Check_green_icon.svg/1200px-Check_green_icon.svg.png")
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun drawSomeState(someState: SomeState) {

    Text(text = "Counter = ${someState.count}")
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
    return UiState.Success(Coil.imageLoader(context)
        .execute(request).drawable?.toBitmap()?.asImageAsset())
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
    url: String,
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
            Image(asset = it,
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