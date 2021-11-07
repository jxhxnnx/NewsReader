package at.technikum_wien.if19b173.newsreader.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import at.technikum_wien.if19b173.newsreader.data.DownloadRSS
import at.technikum_wien.if19b173.newsreader.data.Parser
import at.technikum_wien.if19b173.newsreader.database.ApplicationDatabase
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModel
import kotlinx.coroutines.*
import java.net.URL

@DelicateCoroutinesApi
public class ReloadNewUrlWorker(appContext : Context,
                              workerParameters : WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    companion object {
        val LOG_TAG: String = ReloadDataWorker::class.java.simpleName
    }


    override suspend fun doWork(): Result {
        Log.d(LOG_TAG, "Reload Data Worker started")
        val db = ApplicationDatabase.getDatabase(applicationContext).itemDao()
        val url = inputData.getString("Url")
        GlobalScope.launch(Dispatchers.Default) {
            try {
                val feed = Parser().parse(DownloadRSS().initInputStream(URL(url))!!)
                withContext(Dispatchers.IO) {
                    db.deleteAll()

                    for (item in feed) {
                        db.insert(item)
                    }
                }
            } catch (er: Exception) {
                Log.w(NewsViewModel.LOG_TAG, "CHECKPOINT " + er)

            }
        }
        return Result.success()
    }
}
