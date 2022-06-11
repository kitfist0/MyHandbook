package my.handbook.data.local.source

import androidx.room.Dao
import androidx.room.Query
import my.handbook.data.local.model.SearchResult

@Dao
interface ParagraphDao {

    @Query(
        """
        SELECT file, section, snippet(paragraphs_fts, '<b>', '</b>', '') text FROM paragraphs
        JOIN paragraphs_fts ON id = docId
        WHERE paragraphs_fts MATCH :query ORDER BY id
        """
    )
    suspend fun searchParagraphs(query: String): List<SearchResult>

    @Query(
        """
        SELECT snippet(paragraphs_fts) FROM paragraphs
        JOIN paragraphs_fts ON id = docId
        WHERE paragraphs_fts MATCH :query
        """
    )
    suspend fun searchText(query: String): List<String>
}
