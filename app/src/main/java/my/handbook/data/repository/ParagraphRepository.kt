package my.handbook.data.repository

import my.handbook.data.db.PorterStemmer
import my.handbook.data.db.dao.ParagraphDao
import my.handbook.data.db.entity.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParagraphRepository @Inject constructor(
    private val paragraphDao: ParagraphDao,
    private val stemmer: PorterStemmer
) {

    suspend fun getSearchResults(searchString: String): List<SearchResult> {
        val searchRequest = searchString.stem()
        return paragraphDao.searchParagraphs(searchRequest)
    }

    suspend fun getTextSnippets(searchString: String): List<String> {
        val searchRequest = searchString.stem()
        return paragraphDao.searchText(searchRequest)
    }

    private fun String.stem() = this
        .replace("\"(\\[\"]|.*)?\"".toRegex(), " ")
        .split("[^\\p{Alpha}]+".toRegex())
        .filter { it.isNotBlank() }
        .map(stemmer::stem)
        .filter { it.length > 2 }
        .joinToString(separator = " OR ", transform = { "$it*" })
}
