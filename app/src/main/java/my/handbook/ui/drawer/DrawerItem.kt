package my.handbook.ui.drawer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import my.handbook.data.entity.Section
import my.handbook.data.entity.Product

/**
 * A sealed class which encapsulates all objects [DrawerAdapter] is able to display.
 */
sealed class DrawerItem {

    data class SectionItem(
        val section: Section,
    ) : DrawerItem()

    data class DividerItem(
        @StringRes val titleRes: Int,
    ) : DrawerItem()

    data class LinkItem(
        val link: String,
        @StringRes val titleRes: Int,
        @DrawableRes val iconRes: Int,
    ) : DrawerItem()

    data class ProductItem(
        val product: Product,
    ) : DrawerItem()
}
