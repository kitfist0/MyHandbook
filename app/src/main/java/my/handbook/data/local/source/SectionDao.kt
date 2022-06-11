package my.handbook.data.local.source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import my.handbook.data.local.model.Section

@Dao
interface SectionDao {

    @Query("SELECT id FROM sections WHERE selected = 1")
    fun getSelectedSectionIds(): Flow<List<Int>>

    @Query("SELECT * FROM sections ORDER BY id")
    fun getSections(): Flow<List<Section>>

    @Update
    suspend fun update(section: Section)
}
