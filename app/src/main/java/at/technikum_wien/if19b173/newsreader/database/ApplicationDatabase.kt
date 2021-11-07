package at.technikum_wien.if19b173.newsreader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import at.technikum_wien.if19b173.newsreader.data.Converter
import at.technikum_wien.if19b173.newsreader.models.NewsItem

@Database(entities = [NewsItem::class], version = 1)
@TypeConverters(Converter::class)
public abstract class ApplicationDatabase : RoomDatabase(){
    abstract fun itemDao() : NewsItemDAO

    companion object {
        @Volatile
        private var INSTANCE : ApplicationDatabase? = null

        fun getDatabase(context: Context) : ApplicationDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val tempInstance2 = INSTANCE
                if (tempInstance2 != null) {
                    return tempInstance2
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApplicationDatabase::class.java,
                    "newsreader"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}