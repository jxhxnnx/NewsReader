package at.technikum_wien.if19b173.newsreader.data

import androidx.room.TypeConverter


class Converter {
    @TypeConverter
    fun fromList(value: List<String>): String {
        val separator = "-"
        return value.joinToString(separator)
    }

    @TypeConverter
    fun stringToList(string : String): List<String> {
        return string.split("-").toList()
    }
}
