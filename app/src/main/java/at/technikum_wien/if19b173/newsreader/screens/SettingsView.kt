package at.technikum_wien.if19b173.newsreader

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModel
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
@Composable
fun SettingsView(navController : NavController, viewModel : NewsViewModel) {

    val currentUrl by viewModel.url.observeAsState()
    var newUrl by remember { mutableStateOf(currentUrl)}
    val displayImages by viewModel.displayImages.observeAsState()
    var newDisplayImages by remember { mutableStateOf(displayImages)}

    BackHandler {
        viewModel.updateUrl(newUrl = newUrl ?: "")
        navController.navigateUp()
    }

    Column{
        Modifier
            .fillMaxWidth()
            .padding(10.dp)

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = newUrl ?: "",
            onValueChange = { newUrl = it },
            label = { Text(text = stringResource(R.string.newsFeedUrl))}
        )

        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Modifier.padding(10.dp)
            Column{
                Modifier.padding(10.dp)
                Text(
                    text = stringResource(R.string.displayImages),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = stringResource(R.string.imagesWillBeDisplayed))
            }
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = displayImages ?: true,
                onCheckedChange = {
                    newDisplayImages = it
                    viewModel.updateDisplayImages(newDisplayImages = newDisplayImages ?: true)},
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Modifier.padding(10.dp)
            Column{
                Modifier.padding(10.dp)
                Text(
                    text = stringResource(R.string.downloadImagesInBackground),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.ImagesWillBeDownLoadingInBackGround))
            }
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = false,
                onCheckedChange = {},
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}