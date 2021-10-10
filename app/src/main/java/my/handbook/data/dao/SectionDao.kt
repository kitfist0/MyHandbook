package my.handbook.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import my.handbook.data.entity.Section

@Dao
interface SectionDao {

    @Query("SELECT id FROM sections WHERE selected = 1")
    fun getSelectedSectionIds(): Flow<List<Int>>

    @Query("SELECT * FROM sections ORDER BY id")
    suspend fun getSections(): List<Section>

    @Update
    suspend fun update(section: Section)
}
