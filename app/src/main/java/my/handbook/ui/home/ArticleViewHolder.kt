package my.handbook.ui.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import my.handbook.R
import my.handbook.data.entity.Article
import my.handbook.databinding.ItemArticleBinding
import my.handbook.util.ItemSwipeActionDrawable
import my.handbook.util.ReboundingSwipeActionCallback
import kotlin.math.abs

class ArticleViewHolder(
    private val binding: ItemArticleBinding,
    listener: ArticleAdapter.ArticleAdapterListener,
): RecyclerView.ViewHolder(binding.root), ReboundingSwipeActionCallback.ReboundableViewHolder {

    companion object {

        // We have to update the shape appearance itself to have the MaterialContainerTransform pick up
        // the correct shape appearance, since it doesn't have access to the MaterialShapeDrawable
        // interpolation. If you don't need this work around, prefer using MaterialShapeDrawable's
        // interpolation property, or in the case of MaterialCardView, the progress property.
        private fun MaterialCardView.updateCardViewTopLeftCornerSize(interpolation: Float) {
            val cornerSize = resources.getDimension(R.dimen.handbook_small_component_corner_radius)
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setTopLeftCornerSize(interpolation * cornerSize)
                .build()
        }
    }

    override val reboundableView: View = binding.articleCard

    init {
        binding.run {
            this.listener = listener
            root.background = ItemSwipeActionDrawable(root.context)
        }
    }

    fun bind(item: Article) {
        binding.item = item
        binding.root.isActivated = item.favorite

        // Setting interpolation here controls whether or not we draw the top left corner as
        // rounded or squared. Since all other corners are set to 0dp rounded, they are
        // not affected.
        binding.articleCard.updateCardViewTopLeftCornerSize(if (item.favorite) 1F else 0F)

        binding.executePendingBindings()
    }

    override fun onReboundOffsetChanged(
        currentSwipePercentage: Float,
        swipeThreshold: Float,
        currentTargetHasMetThresholdOnce: Boolean
    ) {
        // Only alter shape and activation in the forward direction once the swipe
        // threshold has been met. Undoing the swipe would require releasing the item and
        // re-initiating the swipe.
        if (currentTargetHasMetThresholdOnce) return

        val isFavorite = binding.item?.favorite ?: false

        // Animate the top left corner radius of the article card as swipe happens.
        val interpolation = (currentSwipePercentage / swipeThreshold).coerceIn(0F, 1F)
        val adjustedInterpolation = abs((if (isFavorite) 1F else 0F) - interpolation)
        binding.articleCard.updateCardViewTopLeftCornerSize(adjustedInterpolation)

        // Start the background animation once the threshold is met.
        val thresholdMet = currentSwipePercentage >= swipeThreshold
        val shouldStar = when {
            thresholdMet && isFavorite -> false
            thresholdMet && isFavorite.not() -> true
            else -> return
        }
        binding.root.isActivated = shouldStar
    }

    override fun onRebounded() {
        val item = binding.item ?: return
        binding.listener?.onFavoriteChanged(item)
    }
}
