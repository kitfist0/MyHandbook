package my.handbook.ui.drawer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import my.handbook.data.db.entity.Section
import simple.billing.data.db.Product

/**
 * A sealed class which encapsulates all objects [DrawerAdapter] is able to display.
 */
sealed class DrawerItem {

    data class SectionItem(
        val section: Section
    ) : DrawerItem()

    data class DividerItem(
        @StringRes val titleRes: Int
    ) : DrawerItem()

    data class LinkItem(
        val link: String,
        @StringRes val titleRes: Int,
        @DrawableRes val iconRes: Int
    ) : DrawerItem()

    data class ProductItem(
        val product: Product
    ) : DrawerItem()

    object DrawerItemDiff : DiffUtil.ItemCallback<DrawerItem>() {
        override fun areItemsTheSame(
            oldItem: DrawerItem,
            newItem: DrawerItem
        ): Boolean {
            return when {
                oldItem is SectionItem && newItem is SectionItem ->
                    oldItem.section.id == newItem.section.id
                oldItem is LinkItem && newItem is LinkItem ->
                    oldItem.link == newItem.link
                oldItem is ProductItem && newItem is ProductItem ->
                    oldItem.product.sku == newItem.product.sku
                else -> true
            }
        }
        override fun areContentsTheSame(
            oldItem: DrawerItem,
            newItem: DrawerItem
        ): Boolean {
            return when {
                oldItem is SectionItem && newItem is SectionItem ->
                    oldItem.section == newItem.section
                oldItem is LinkItem && newItem is LinkItem ->
                    oldItem == newItem
                oldItem is ProductItem && newItem is ProductItem ->
                    oldItem.product == newItem.product
                else -> true
            }
        }
    }
}