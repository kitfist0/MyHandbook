package my.handbook.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import my.handbook.data.dao.ParagraphDao
import my.handbook.data.entity.SearchResult
import my.handbook.util.PorterStemmer
import javax.inject.Inject

class SearchParagraphUseCase @Inject constructor(
    private val paragraphDao: ParagraphDao,
    private val stemmer: PorterStemmer,
) {

    companion object {
        private const val DELAY_MILLIS = 300L
    }

    fun execute(searchQuery: String): Flow<UseCaseResult<List<SearchResult>>> = flow {
        emit(UseCaseResult.Loading)
        delay(DELAY_MILLIS)
        val searchingResults = paragraphDao.searchParagraphs(searchQuery.stem())
        emit(UseCaseResult.Success(searchingResults))
    }

    private fun String.stem() = this
        .replace("\"(\\[\"]|.*)?\"".toRegex(), " ")
        .split("[^\\p{Alpha}]+".toRegex())
        .filter { it.isNotBlank() }
        .map(stemmer::stem)
        .filter { it.length > 2 }
        .joinToString(separator = " NEAR ", transform = { "$it*" })
}
