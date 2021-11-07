package at.technikum_wien.if19b173.newsreader.worker

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import at.technikum_wien.if19b173.newsreader.*
import at.technikum_wien.if19b173.newsreader.data.DownloadRSS
import at.technikum_wien.if19b173.newsreader.data.Parser
import at.technikum_wien.if19b173.newsreader.database.ApplicationDatabase
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModel
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL


@DelicateCoroutinesApi
public class DownloadDataWorker(appContext : Context,
                                workerParameters : WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    companion object {
        val LOG_TAG: String = DownloadDataWorker::class.java.simpleName
    }


    override suspend fun doWork(): Result {
        val db = ApplicationDatabase.getDatabase(applicationContext).itemDao()
        val url = inputData.getString("Url")
        val count = db.getCount()
        if (count == 0) {
            try {
                val feed = Parser().parse(DownloadRSS().initInputStream(URL(url))!!)
                withContext(Dispatchers.IO) {
                    for (item in feed) {
                        db.insert(item)
                    }
                }
            } catch (er: Exception) {
                Log.w(NewsViewModel.LOG_TAG, "CHECKPOINT " + er)

            }
            Log.d(LOG_TAG, "Download Data Worker started")

        }
        return Result.success()
    }

}