package my.handbook.usecase

import my.handbook.data.dao.SectionDao
import my.handbook.data.entity.Section
import javax.inject.Inject

class ChangeSectionSelectionUseCase @Inject constructor(
    private val sectionDao: SectionDao,
) {
    suspend fun execute(section: Section) {
        val updatedSection = section.copy(selected = !section.selected)
        sectionDao.update(updatedSection)
    }
}
