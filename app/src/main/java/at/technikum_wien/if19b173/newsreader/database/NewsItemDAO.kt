package at.technikum_wien.if19b173.newsreader.database

import androidx.lifecycle.LiveData
import androidx.room.*
import at.technikum_wien.if19b173.newsreader.models.NewsItem

@Dao
interface NewsItemDAO {
    @get:Query("SELECT * FROM newsItem order by publicationDate desc")
    val items : LiveData<List<NewsItem>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item : NewsItem)
    @Update
    suspend fun update(item : NewsItem)
    @Delete
    suspend fun delete(item : NewsItem)
    @Query("SELECT COUNT(*) FROM newsItem")
    suspend fun getCount() : Int
    @Query("DELETE FROM newsItem")
    suspend fun deleteAll()
}