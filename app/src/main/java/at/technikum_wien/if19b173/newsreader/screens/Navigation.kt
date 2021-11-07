package at.technikum_wien.if19b173.newsreader

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


@DelicateCoroutinesApi
@Composable
fun Navigation(viewModel: NewsViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = Screen.MainView.route ) {
        composable(route = Screen.MainView.route) {
            MainView(navController = navController, viewModel = viewModel)
        }
        composable(route = Screen.DetailsView.route + "?content={content}",
            arguments = listOf(
                navArgument(name = "content") {
                    type = NavType.StringType
                    nullable = true
                })) {
            DetailsView(contentId = Json.decodeFromString<String>(it.arguments?.getString("content") ?: ""), navController = navController, viewModel = viewModel)
        }
        composable(route = Screen.SettingsView.route) {
            SettingsView(navController = navController, viewModel = viewModel)
        }
    }
}