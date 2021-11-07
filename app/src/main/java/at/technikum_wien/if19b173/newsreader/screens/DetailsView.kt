package at.technikum_wien.if19b173.newsreader


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import at.technikum_wien.if19b173.newsreader.models.NewsItem
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModel
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@Composable
fun DetailsView(contentId : String, navController : NavController, viewModel : NewsViewModel) {
    val scrollState = rememberScrollState()
    var content = NewsItem(stringResource(R.string.Loading), "", "", "", "", "", "", listOf(""))
    val contentList by viewModel.items.observeAsState()
    val displayImage by viewModel.displayImages.observeAsState()
    val uriHandler = LocalUriHandler.current
    for (item in contentList ?: emptyList()) {
        if (item.id == contentId) {
            content = item
        }
    }
    Column{

        TopAppBar(
            title = {
                Text(text = stringResource(R.string.header))
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        Box(
            Modifier
                .padding(8.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            Column() {
                Box()
                {
                    if (displayImage!!)
                    {
                        GlideImage(
                            imageModel = content.image,
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
                            .background(Color.DarkGray.copy(alpha = 0.6f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = content.title,
                            fontSize = 21.sp,
                            color = Color.White
                        )
                        Text(
                            text = content.author,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = content.publicationDate,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = content.description,
                    maxLines = 20
                )

                Spacer(modifier = Modifier.height(8.dp))

                for (word in content.keyWord) {
                    Text(
                        text = word,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { uriHandler.openUri(content.link) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.viewFullStory))
                }
            }
        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview2() {
//    NewsReaderTheme {
//    DetailsView()
//    }
//}