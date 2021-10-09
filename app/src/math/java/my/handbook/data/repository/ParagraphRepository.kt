package my.handbook.data.repository

import android.util.Log
import my.handbook.data.db.dao.ParagraphDao
import my.handbook.data.db.entity.SearchResult

class ParagraphRepository(
    paragraphDao: ParagraphDao,
    private val stemmer: PorterStemmer,
) : BaseParagraphRepository(paragraphDao) {

    override suspend fun getSearchResults(searchRequest: String): List<SearchResult> {
        return super.getSearchResults(searchRequest.stem())
    }

    private fun String.stem() = this
        .replace("\"(\\[\"]|.*)?\"".toRegex(), " ")
        .split("[^\\p{Alpha}]+".toRegex())
        .filter { it.isNotBlank() }
        .also { Log.d("ParagraphRepository", "words: $it") }
        .map(stemmer::stem)
        .filter { it.length > 2 }
        .joinToString(separator = " NEAR ", transform = { "$it*" })
        .also { Log.d("ParagraphRepository", "searchRequest: $it") }
}
