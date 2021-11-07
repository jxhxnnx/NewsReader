package at.technikum_wien.if19b173.newsreader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Image
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModel
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@DelicateCoroutinesApi
@Composable
fun MainView(navController: NavController, viewModel: NewsViewModel) {
    val content by viewModel.items.observeAsState()
    var expanded  by remember { mutableStateOf(false) }
    val displayImage by viewModel.displayImages.observeAsState()
    val errorMsg by viewModel.errorMsg.observeAsState()
    val err by viewModel.err.observeAsState()

    Column{
        TopAppBar(
            title = { Text(text = stringResource(R.string.header)) },
            actions = {
                IconButton(onClick = {
                    expanded = true
                }) {
                    Icon(Icons.Filled.MoreVert, stringResource(R.string.menu))
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                        expanded = false
                        }) {
                        DropdownMenuItem(onClick = {
                            navController.navigate(Screen.SettingsView.route)
                        }) {
                            Text(stringResource(R.string.settings))
                        }
                        DropdownMenuItem(onClick = {
                            viewModel.reloadData()
                        }) {
                            Text(stringResource(R.string.reload))
                        }
                    }
                }
            }
        )
        if(err == true){
            Text(text = errorMsg ?: "")
        }

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
            content?.let {
                items(it.size) { index ->
                    content?.get(index)?.let {
                        Row(Modifier
                            .clickable {
                                navController.navigate(
                                    Screen.DetailsView.route +
                                            "?content=${Json.encodeToString(content!![index].id)}"
                                )
                            }) {
                            if(index == 0) {
                                Box()
                                {
                                    if(displayImage!!)
                                    {
                                        GlideImage(
                                            imageModel = it.image,
                                            contentScale = ContentScale.FillWidth,
                                            circularReveal = CircularReveal(duration = 250),
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            placeHolder = Icons.Outlined.Image,
                                            error = Icons.Outlined.Error
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .background(
                                                androidx.compose.ui.graphics.Color.DarkGray.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = it.title,
                                            fontSize = 21.sp,
                                            color = androidx.compose.ui.graphics.Color.White)
                                        Text(
                                            text = it.author,
                                            fontSize = 12.sp,
                                            color = androidx.compose.ui.graphics.Color.White)
                                        Text(
                                            text = it.publicationDate,
                                            fontSize = 12.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = androidx.compose.ui.graphics.Color.White
                                        )
                                    }
                                }
                            }
                            else {
                                if(displayImage!!)
                                {
                                    GlideImage(
                                        imageModel = it.image,
                                        contentScale = ContentScale.Fit,
                                        circularReveal = CircularReveal(duration = 250),
                                        modifier = Modifier.size(80.dp),
                                        placeHolder = Icons.Outlined.Image,
                                        error = Icons.Outlined.Error
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Text(
                                        text = it.title,
                                        fontSize = 21.sp,
                                        maxLines = 2)
                                    Text(
                                        text = it.author,
                                        fontSize = 12.sp)
                                    Text(
                                        text = it.publicationDate,
                                        fontSize = 12.sp,
                                        fontStyle = FontStyle.Italic
                                    )
                                    Divider(color = androidx.compose.ui.graphics.Color.LightGray, thickness = 1.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}