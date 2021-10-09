package my.handbook.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import my.handbook.data.db.dao.SectionDao
import my.handbook.data.db.entity.Section
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SectionRepository @Inject constructor(
    private val sectionDao: SectionDao,
) {

    fun getSelectedSectionIds(): Flow<List<Int>> = sectionDao.getSelectedSectionIds()

    fun getSections(): Flow<List<Section>> = sectionDao.getSections()

    suspend fun updateSection(section: Section) {
        val updatedSection = Section(section.id, !section.selected)
        sectionDao.update(updatedSection)
    }
}
