package my.handbook.ui.home

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import my.handbook.R
import my.handbook.data.entity.Article
import my.handbook.databinding.FragmentHomeBinding
import my.handbook.ui.base.BaseFragment
import my.handbook.util.ReboundingSwipeActionCallback

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), ArticleAdapter.ArticleAdapterListener {

    override val layoutRes: Int = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModels()

    private val articleAdapter = ArticleAdapter(this)

    override fun initViews() {
        binding.homeRecyclerView.apply {
            val itemTouchHelper = ItemTouchHelper(ReboundingSwipeActionCallback())
            itemTouchHelper.attachToRecyclerView(this)
            adapter = articleAdapter
        }
        binding.homeRecyclerView.adapter = articleAdapter
    }

    override fun observeData() {
        viewModel.articles.observe(viewLifecycleOwner) {
            articleAdapter.submitList(it)
        }
    }

    override fun onArticleClicked(file: String) {
        viewModel.onArticleClicked(file)
    }

    override fun onFavoriteChanged(item: Article) {
        viewModel.onFavoriteChanged(item)
    }
}
