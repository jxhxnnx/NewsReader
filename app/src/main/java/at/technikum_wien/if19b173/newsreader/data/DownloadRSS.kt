package at.technikum_wien.if19b173.newsreader.data


import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.net.URL


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