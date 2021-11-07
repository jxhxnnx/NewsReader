package at.technikum_wien.if19b173.newsreader.worker

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import at.technikum_wien.if19b173.newsreader.data.DownloadRSS
import at.technikum_wien.if19b173.newsreader.data.Parser
import at.technikum_wien.if19b173.newsreader.database.ApplicationDatabase
import at.technikum_wien.if19b173.newsreader.viewModel.NewsViewModel
import kotlinx.coroutines.*
import java.net.URL
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@DelicateCoroutinesApi
public class KillAfterFiveDayWorker(appContext : Context,
                                workerParameters : WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    companion object {
        val LOG_TAG: String = ReloadDataWorker::class.java.simpleName
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        Log.d(LOG_TAG, "Reload Data Worker started")
        val db = ApplicationDatabase.getDatabase(applicationContext).itemDao()
        val items = db.items
        GlobalScope.launch(Dispatchers.Default) {
            try {
                if(items.value != null)
                {
                    for(item in items.value!!)
                    {
                        val date = LocalDate.parse(item.publicationDate, DateTimeFormatter.ISO_LOCAL_DATE)
                        if(Duration.between(date, LocalDate.now()).toDays() >= 5)
                        {
                            db.delete(item)
                        }
                    }
                }


            } catch (er: Exception) {
                Log.w(NewsViewModel.LOG_TAG, "CHECKPOINT $er")

            }
        }
        return Result.success()
    }
}
