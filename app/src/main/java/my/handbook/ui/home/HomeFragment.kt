package my.handbook.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import my.handbook.data.db.entity.Article
import my.handbook.databinding.FragmentHomeBinding
import my.handbook.util.ReboundingSwipeActionCallback

@AndroidEntryPoint
class HomeFragment : Fragment(), ArticleAdapter.ArticleAdapterListener {

    private val viewModel: HomeViewModel by viewModels()
    private val articleAdapter = ArticleAdapter(this)
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            val itemTouchHelper = ItemTouchHelper(ReboundingSwipeActionCallback())
            itemTouchHelper.attachToRecyclerView(this)
            adapter = articleAdapter
        }
        binding.recyclerView.adapter = articleAdapter

        viewModel.articles.observe(viewLifecycleOwner) {
            articleAdapter.submitList(it)
        }
    }

    override fun onArticleClicked(cardView: View, file: String) {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToReadFragment(file))
    }

    override fun onFavoriteChanged(item: Article) {
        viewModel.onFavoriteClicked(item)
    }
}
