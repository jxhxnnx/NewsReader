package at.technikum_wien.if19b173.newsreader.models

import androidx.annotation.NonNull
import androidx.room.*

@kotlinx.serialization.Serializable
@Entity(tableName = "newsItem", indices = [Index(value = ["id"], unique = true)])
data class NewsItem(
    @ColumnInfo var title : String,
    @ColumnInfo var description : String,
    @ColumnInfo var image : String,
    @ColumnInfo var link : String,
    @PrimaryKey @ColumnInfo @NonNull var id : String,
    @ColumnInfo var author : String,
    @ColumnInfo var publicationDate : String,
    @ColumnInfo var keyWord : List<String>)

