package at.technikum_wien.if19b173.newsreader


import android.util.Xml
import android.widget.Toast
import androidx.core.text.HtmlCompat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

private val ns: String? = null

class Parser {

    companion object {
        val LOG_TAG = Parser::class.simpleName
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<NewsItem> {
        if (inputStream != null) {
            inputStream.use { inputStream ->
                val parser: XmlPullParser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(inputStream, null)
                parser.nextTag()
                return readFeed(parser)
            }
        } else {
            Toast.makeText(MainActivity(), "Error! Try again later", Toast.LENGTH_LONG).show()
            return emptyList()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<NewsItem> {
        val newsItem = mutableListOf<NewsItem>()

        parser.require(XmlPullParser.START_TAG, ns, "rss")
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "channel") {
                parser.require(XmlPullParser.START_TAG, ns, "channel")
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.name == "item") {
                        newsItem.add(readEntry(parser))
                    } else {
                        skip(parser)
                    }
                }

            } else {
                skip(parser)
            }
        }
        return newsItem
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): NewsItem {
        parser.require(XmlPullParser.START_TAG, ns, "item")
        var title: String? = null
        var description: String? = null
        var image: String? = null
        var link: String? = null
        var id: String? = null
        var author: String? = null
        var pubDate: String? = null
        val category = mutableListOf<String>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> title = readContent(parser, "title")?.trim()
                "description" -> description = HtmlCompat.fromHtml(
                    readContent(parser, "description")!!,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ).toString()
                "media:content" -> {
                    val newImage = readImage(parser)
                    if (newImage != null) image = newImage
                }
                "link" -> link = readContent(parser, "link")
                "guid" -> id = readContent(parser, "guid")
                "dc:creator" -> author = readContent(parser, "dc:creator")?.trim()
                "pubDate" -> pubDate = readContent(parser, "pubDate")
                "category" -> category.add(readContent(parser, "category")?.trim()!!)

                else -> skip(parser)
            }
        }
        return NewsItem(
            title ?: "title",
            description ?: "desc",
            image ?: "desc",
            link ?: "desc",
            id ?: "desc",
            author ?: "desc",
            pubDate ?: "desc",
            category ?: emptyList()
        )
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String? {
        var result : String? = null
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readContent(parser: XmlPullParser, tag: String): String? {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        val result = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return result
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readImage(parser: XmlPullParser): String? {
        var url : String? = null
        var image = false
        var type : String? = null

        parser.require(XmlPullParser.START_TAG, ns, "media:content")
        for (i in 0 until parser.attributeCount) {
            if (parser.getAttributeName(i) == "medium") {
                if (parser.getAttributeValue(i) == "image") {
                    image = true
                }
            }
            if (parser.getAttributeName(i) == "url") {
                url = parser.getAttributeValue(i)
            }
        }
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "media:keywords" -> type = readContent(parser, "media:keywords")
                else -> skip(parser)
            }
        }
        return if (type == "headline" && image) url else null
    }





}