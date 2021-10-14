package at.technikum_wien.if19b173.newsreader


import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.net.URL
import kotlinx.coroutines.flow.collect

@DelicateCoroutinesApi
class NewsViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
    var url = MutableLiveData<String>()
    var displayImages = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect {
                if(it.url != ""){
                    url.value = it.url
                    loadEntries(entriesInt)
                }
                else
                {
                    url.value = "https://www.engadget.com/rss.xml"
                    loadEntries(entriesInt)
                }
                displayImages.value = it.displayImages
            }
        }
    }

    fun updateUrl(newUrl : String) {
        viewModelScope.launch {
            userPreferencesRepository.updateUrl(newUrl = newUrl)
            loadEntries(entriesInt)
        }
    }
    fun updateDisplayImages(newDisplayImages : Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDisplayImages(newDisplayImages)
        }
    }

    fun reload() {
        loadEntries(entriesInt)
    }

    companion object {
        val LOG_TAG = NewsViewModel::class.simpleName
    }

        private val entriesInt : MutableLiveData<List<NewsItem>> by lazy {

            MutableLiveData<List<NewsItem>>().also {
                loadEntries(it)

            }
        }


        private fun loadEntries(liveData : MutableLiveData<List<NewsItem>>) {
            GlobalScope.launch(Dispatchers.Default){
                //Log.w(LOG_TAG, "LoadEntries")
                try{
                    liveData.postValue(Parser().parse(DownloadRSS().initInputStream(URL(url.value))!!))


                } catch(e: Throwable){
                    Log.w(LOG_TAG, "EXCEPTION")
                    liveData.postValue(listOf(NewsItem("", "", "", "", "", "", "", emptyList())))
                }
            }
        }

        val outputText : LiveData<List<NewsItem>>
            get() = entriesInt

    }

@DelicateCoroutinesApi
class ViewModelFactory(private val userPreferencesRepository: UserPreferencesRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}