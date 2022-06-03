package my.handbook.ui.drawer

import androidx.recyclerview.widget.DiffUtil

object DrawerItemDiff : DiffUtil.ItemCallback<DrawerItem>() {

    override fun areItemsTheSame(
        oldItem: DrawerItem,
        newItem: DrawerItem
    ): Boolean {
        return when {
            oldItem is DrawerItem.SectionItem && newItem is DrawerItem.SectionItem ->
                oldItem.section.id == newItem.section.id
            oldItem is DrawerItem.LinkItem && newItem is DrawerItem.LinkItem ->
                oldItem.link == newItem.link
            oldItem is DrawerItem.CoffeeItem && newItem is DrawerItem.CoffeeItem ->
                oldItem.id == newItem.id
            else -> true
        }
    }

    override fun areContentsTheSame(
        oldItem: DrawerItem,
        newItem: DrawerItem
    ): Boolean {
        return when {
            oldItem is DrawerItem.SectionItem && newItem is DrawerItem.SectionItem ->
                oldItem.section == newItem.section
            oldItem is DrawerItem.LinkItem && newItem is DrawerItem.LinkItem ->
                oldItem == newItem
            oldItem is DrawerItem.CoffeeItem && newItem is DrawerItem.CoffeeItem ->
                oldItem.isPurchased == newItem.isPurchased
            else -> true
        }
    }
}
