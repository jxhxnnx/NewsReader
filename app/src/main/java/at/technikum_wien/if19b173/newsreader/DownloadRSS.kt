package at.technikum_wien.if19b173.newsreader


import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class DownloadRSS {

    suspend fun initInputStream(url: URL): InputStream? {
        return try {
             withContext(Dispatchers.IO) {url.openConnection().getInputStream()}
        }
        catch (e: IOException) {
            null
        }
    }
}