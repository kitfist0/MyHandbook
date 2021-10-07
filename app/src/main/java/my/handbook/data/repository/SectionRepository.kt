package my.handbook.data.repository

import kotlinx.coroutines.flow.Flow
import my.handbook.data.db.dao.SectionDao
import my.handbook.data.db.entity.Section
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SectionRepository @Inject constructor(
    private val sectionDao: SectionDao,
) {

    fun getSelectedSections(): Flow<List<Int>> = sectionDao.getSelectedSections()

    fun getSections(): Flow<List<Section>> = sectionDao.getSections()

    suspend fun updateSection(section: Section) {
        val updatedSection = Section(section.id, !section.selected)
        sectionDao.update(updatedSection)
    }
}
