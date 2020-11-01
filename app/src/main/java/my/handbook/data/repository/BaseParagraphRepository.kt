package my.handbook.data.repository

import my.handbook.data.db.dao.ParagraphDao
import my.handbook.data.db.entity.SearchResult

open class BaseParagraphRepository(
    private val paragraphDao: ParagraphDao
) {

    open suspend fun getSearchResults(searchRequest: String): List<SearchResult> {
        return paragraphDao.searchParagraphs(searchRequest)
    }

    open suspend fun getTextSnippets(searchRequest: String): List<String> {
        return paragraphDao.searchText(searchRequest)
    }
}