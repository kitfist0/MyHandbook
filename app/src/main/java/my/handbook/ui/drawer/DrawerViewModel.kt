package my.handbook.ui.drawer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import my.handbook.BuildConfig
import my.handbook.R
import my.handbook.data.repository.SectionRepository

class DrawerViewModel @ViewModelInject constructor(
    private val sectionRepository: SectionRepository
) : ViewModel() {

    companion object {
        val dividersAndLinks = mutableListOf(
            DrawerItem.DividerItem(R.string.about),
            DrawerItem.LinkItem(
                link = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}",
                titleRes = R.string.rate_app,
                iconRes = R.drawable.ic_twotone_grade
            ),
            DrawerItem.LinkItem(
                link = BuildConfig.GITHUB,
                titleRes = R.string.github,
                iconRes = R.drawable.ic_twotone_github
            ),
            DrawerItem.LinkItem(
                link = BuildConfig.LICENSE,
                titleRes = R.string.license,
                iconRes = R.drawable.ic_twotone_copyright
            )
        )
    }

    private val sections = liveData {
        sectionRepository.getSections().collect { emit(it) }
    }

    val drawerItems = sections.switchMap {
        liveData {
            val items = it.map { section -> DrawerItem.SectionItem(section) } + dividersAndLinks
            emit(items)
        }
    }

    fun onSectionClicked(sectionItem: DrawerItem.SectionItem) =
        viewModelScope.launch { sectionRepository.updateSection(sectionItem.section) }
}
