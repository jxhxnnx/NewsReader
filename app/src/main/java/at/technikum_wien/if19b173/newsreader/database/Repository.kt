package at.technikum_wien.if19b173.newsreader.database

import at.technikum_wien.if19b173.newsreader.models.NewsItem

class Repository(private val itemDAO : NewsItemDAO) {
    val items = itemDAO.items

    suspend fun insert(item: NewsItem) {
        itemDAO.insert(item)
    }

    suspend fun update(item: NewsItem) {
        itemDAO.update(item)
    }

    suspend fun delete(item: NewsItem) {
        itemDAO.delete(item)
    }
}