package my.handbook.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import my.handbook.data.db.entity.Section

@Dao
interface SectionDao {

    @Query("SELECT id FROM sections WHERE selected = 1")
    fun getSelectedSections(): Flow<List<Int>>

    @Query("SELECT * FROM sections ORDER BY id")
    fun getSections(): Flow<List<Section>>

    @Update
    suspend fun update(section: Section)
}
