package at.technikum_wien.if19b173.newsreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import at.technikum_wien.if19b173.newsreader.ui.theme.NewsReaderTheme
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModel
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel =
            ViewModelProvider(this,
                NewsViewModelFactory(
                    UserPreferencesRepository(dataStore),
                    this.application
                ))[NewsViewModel::class.java]
        setContent {
            NewsReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                }
                Navigation(viewModel)
            }
        }
    }
}

sealed class Screen(val route : String) {
    object MainView : Screen(route = "MainView")
    object DetailsView : Screen(route = "DetailsView")
    object SettingsView : Screen(route = "SettingsView")
}

@Composable
fun BackHandler(enabled : Boolean = true, onBack : () -> Unit) {
    val currentOnBack by rememberUpdatedState(onBack)
    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
    }
    SideEffect {
        backCallback.isEnabled = enabled
    }
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}






//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    NewsReaderTheme {
//    MainView()
//    }
//}

