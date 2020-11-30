package my.handbook.ui.drawer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import my.handbook.databinding.ItemDrawerDividerBinding
import my.handbook.databinding.ItemDrawerLinkBinding
import my.handbook.databinding.ItemDrawerProductBinding
import my.handbook.databinding.ItemDrawerSectionBinding

sealed class DrawerItemViewHolder<T : DrawerItem>(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item : T)

    class SectionViewHolder(
        private val binding: ItemDrawerSectionBinding,
        private val adapterListener: DrawerAdapter.DrawerAdapterListener
    ) : DrawerItemViewHolder<DrawerItem.SectionItem>(binding.root) {

        override fun bind(item: DrawerItem.SectionItem) {
            binding.run {
                sectionItem = item
                listener = adapterListener
                executePendingBindings()
            }
        }
    }

    class DividerViewHolder(
        private val binding: ItemDrawerDividerBinding
    ) : DrawerItemViewHolder<DrawerItem.DividerItem>(binding.root) {

        override fun bind(item: DrawerItem.DividerItem) {
            binding.dividerItem = item
            binding.executePendingBindings()
        }
    }

    class LinkViewHolder(
        private val binding: ItemDrawerLinkBinding,
        private val adapterListener: DrawerAdapter.DrawerAdapterListener
    ) : DrawerItemViewHolder<DrawerItem.LinkItem>(binding.root) {

        override fun bind(item: DrawerItem.LinkItem) {
            binding.run {
                linkItem = item
                listener = adapterListener
                executePendingBindings()
            }
        }
    }

    class ProductViewHolder(
        private val binding: ItemDrawerProductBinding,
        private val adapterListener: DrawerAdapter.DrawerAdapterListener
    ) : DrawerItemViewHolder<DrawerItem.ProductItem>(binding.root) {

        override fun bind(item: DrawerItem.ProductItem) {
            binding.run {
                productItem = item
                listener = adapterListener
                executePendingBindings()
            }
        }
    }
}
