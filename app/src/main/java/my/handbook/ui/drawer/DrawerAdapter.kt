package my.handbook.ui.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import my.handbook.databinding.ItemDrawerDividerBinding
import my.handbook.databinding.ItemDrawerLinkBinding
import my.handbook.databinding.ItemDrawerSectionBinding

class DrawerAdapter(
    private val listener: DrawerAdapterListener
) : ListAdapter<DrawerItem, DrawerItemViewHolder<DrawerItem>>(
    DrawerItem.DrawerItemDiff
) {

    companion object {
        private const val VIEW_TYPE_SECTION = 1
        private const val VIEW_TYPE_DIVIDER = 2
        private const val VIEW_TYPE_LINK = 3
    }

    interface DrawerAdapterListener {
        fun onSectionClicked(item: DrawerItem.SectionItem)
        fun onLinkClicked(item: DrawerItem.LinkItem)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DrawerItem.SectionItem -> VIEW_TYPE_SECTION
            is DrawerItem.DividerItem -> VIEW_TYPE_DIVIDER
            is DrawerItem.LinkItem -> VIEW_TYPE_LINK
            else -> throw RuntimeException("Unsupported ItemViewType for obj ${getItem(position)}")
        }
    }

    @Suppress("unchecked_cast")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DrawerItemViewHolder<DrawerItem> {
        return when (viewType) {
            VIEW_TYPE_SECTION -> DrawerItemViewHolder.SectionViewHolder(
                ItemDrawerSectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            VIEW_TYPE_DIVIDER -> DrawerItemViewHolder.DividerViewHolder(
                ItemDrawerDividerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_LINK -> DrawerItemViewHolder.LinkViewHolder(
                ItemDrawerLinkBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            else -> throw RuntimeException("Unsupported view holder type")
        } as DrawerItemViewHolder<DrawerItem>
    }

    override fun onBindViewHolder(
        holder: DrawerItemViewHolder<DrawerItem>,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}
