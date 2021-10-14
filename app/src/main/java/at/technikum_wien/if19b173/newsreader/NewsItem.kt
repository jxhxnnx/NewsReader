package at.technikum_wien.if19b173.newsreader

@kotlinx.serialization.Serializable
data class NewsItem(var title : String,
                    var description : String,
                    var image : String,
                    var link : String,
                    var id : String,
                    var author : String,
                    var publicationDate : String,
                    var keyWord : List<String>)
