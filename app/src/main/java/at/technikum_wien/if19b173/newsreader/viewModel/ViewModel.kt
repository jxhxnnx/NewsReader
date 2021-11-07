package at.technikum_wien.if19b173.newsreader.viewModel


import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import at.technikum_wien.if19b173.newsreader.MainActivity
import at.technikum_wien.if19b173.newsreader.UserPreferencesRepository
import at.technikum_wien.if19b173.newsreader.data.DownloadRSS
import at.technikum_wien.if19b173.newsreader.data.Parser
import at.technikum_wien.if19b173.newsreader.database.ApplicationDatabase
import at.technikum_wien.if19b173.newsreader.database.Repository
import at.technikum_wien.if19b173.newsreader.models.NewsItem
import at.technikum_wien.if19b173.newsreader.worker.DownloadDataWorker
import at.technikum_wien.if19b173.newsreader.worker.ReloadDataWorker
import at.technikum_wien.if19b173.newsreader.worker.ReloadNewUrlWorker
import kotlinx.coroutines.*
import java.net.URL
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit

@DelicateCoroutinesApi
class NewsViewModel(private val userPreferencesRepository: UserPreferencesRepository, application : Application) : ViewModel() {
    companion object {
        val LOG_TAG = NewsViewModel::class.simpleName
    }

    private val repository: Repository
    private val newsItemList by lazy { MutableLiveData<List<NewsItem>>(listOf()) }
    val errorMsg by lazy { MutableLiveData<String?>(null) }
    private val _err = MutableLiveData(false)


    val items: LiveData<List<NewsItem>>
    val err: LiveData<Boolean>
        get() = _err
    var url = MutableLiveData<String>()
    var displayImages = MutableLiveData<Boolean>()
    var downloadImages = MutableLiveData<Boolean>()
    private val app = application

    init {
        val itemDAO = ApplicationDatabase.getDatabase(application).itemDao()
        repository = Repository(itemDAO)
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect {
                displayImages.value = it.displayImages
                downloadImages.value = it.downloadImages
                url.value = it.url
            }
        }
        items = repository.items
        if(url.value == null)
        {
            url.value = "https://www.engadget.com/rss.xml"
        }
        if (items.value == null) {
            initDataBase()
            Log.w(LOG_TAG, "CHECKPOINT INIT")
        }
        halfHourLoading()
        killAfterFiveDays()
    }

    private fun halfHourLoading()
    {
        val workerData : Data = workDataOf("Url" to url.value)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = PeriodicWorkRequest.Builder(DownloadDataWorker::class.java, 30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(workerData)
            .build()
        WorkManager
            .getInstance(app)
            .enqueueUniquePeriodicWork(ReloadDataWorker::class.java.simpleName ?: "",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest)
    }

    private fun killAfterFiveDays()
    {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = PeriodicWorkRequest.Builder(DownloadDataWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        WorkManager
            .getInstance(app)
            .enqueueUniquePeriodicWork(ReloadDataWorker::class.java.simpleName ?: "",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest)
    }

    private fun initDataBase()
    {
        Log.w(LOG_TAG, "CHECKPOINT INIT DB")
        val workerData : Data = workDataOf("Url" to url.value)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = OneTimeWorkRequest.Builder(DownloadDataWorker::class.java)
            .setConstraints(constraints)
            .setInputData(workerData)
            .build()
        WorkManager
            .getInstance(app)
            .enqueue(workRequest)
    }

    fun reloadData()
    {
        val workerData : Data = workDataOf("Url" to url.value)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = OneTimeWorkRequest.Builder(ReloadDataWorker::class.java)
            .setConstraints(constraints)
            .setInputData(workerData)
            .build()
        WorkManager
            .getInstance(app)
            .enqueue(workRequest)
    }

    private fun insert(newsItems: List<NewsItem>) = viewModelScope.launch {

        try {
            withContext(Dispatchers.IO)
            {
                for (item in newsItems) {
                    repository.insert(item)
                }
            }
        } catch (ex: Exception) {
            errorMsg.value = "Error"
        }
    }

    private fun delete() = viewModelScope.launch {

        try {
            withContext(Dispatchers.IO)
            {
                for (item in repository.items.value!!) {
                    repository.delete(item)
                }
            }
        } catch (ex: Exception) {
            errorMsg.value = "Error"
        }

    }

    fun updateUrl(newUrl: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateUrl(newUrl = newUrl)
        }
        if(newUrl != url.value)
        {
            updateDataWithNewUrl(newUrl)
        }
    }

    private fun updateDataWithNewUrl(newUrl: String) {

        val workerData : Data = workDataOf("Url" to newUrl)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = OneTimeWorkRequest.Builder(ReloadNewUrlWorker::class.java)
            .setConstraints(constraints)
            .setInputData(workerData)
            .build()
        WorkManager
            .getInstance(app)
            .enqueue(workRequest)
    }

    fun updateDisplayImages(newDisplayImages: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDisplayImages(newDisplayImages)
        }
    }

    fun updateDownLoadImages(newsDownloadImages: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDownloadImages(newsDownloadImages)
        }
    }

    fun updatePreferences(url: String, displayImages: Boolean, downloadImages: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateUrl(newUrl = url)
            userPreferencesRepository.updateDisplayImages(displayImages)
            userPreferencesRepository.updateDownloadImages(downloadImages)
        }
    }

    fun reload() {
        Log.w(LOG_TAG, "CHECKPOINT " + url.value)
        delete()
        url.value?.let { downloadItem(it) }
    }

    fun download()
    {
        url.value?.let { downloadItem(it) }
    }

    private fun downloadItem(url: String) {
        _err.value = false
        newsItemList.value = listOf()
        viewModelScope.launch {
            val feed: List<NewsItem>
            withContext(Dispatchers.IO) {
                Log.w(LOG_TAG, "CHECKPOINT $url")
                feed = Parser().parse(DownloadRSS().initInputStream(URL(url))!!)
            }
            try {
                newsItemList.value = feed
                Log.w(LOG_TAG, "CHECKPOINT DOWNLOAD")
                insert(newsItemList.value!!)

            } catch (er: Exception) {
                errorMsg.value = "Error! Try again later..."
                Log.w(LOG_TAG, "CHECKPOINT " + er)
            }
        }
    }
}












@DelicateCoroutinesApi
class NewsViewModelFactory(private val userPreferencesRepository: UserPreferencesRepository, private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(userPreferencesRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}