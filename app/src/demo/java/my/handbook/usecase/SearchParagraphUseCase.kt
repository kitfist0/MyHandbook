package my.handbook.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import my.handbook.data.db.dao.ParagraphDao
import my.handbook.data.db.entity.SearchResult
import javax.inject.Inject

class SearchParagraphUseCase @Inject constructor(
    private val paragraphDao: ParagraphDao,
) {

    companion object {
        private const val DELAY_MILLIS = 300L
    }

    fun execute(searchQuery: String): Flow<UseCaseResult<List<SearchResult>>> = flow {
        emit(UseCaseResult.Loading)
        delay(DELAY_MILLIS)
        val searchingResults = paragraphDao.searchParagraphs(searchQuery)
        emit(UseCaseResult.Success(searchingResults))
    }
}
