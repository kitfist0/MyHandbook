package my.handbook.ui.read

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import my.handbook.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val url: LiveData<String> = liveData {
        val fileName = ReadFragmentArgs.fromSavedStateHandle(savedStateHandle).fileName
        emit("file:///android_asset/html/${fileName}")
    }

    private val _searchText = MutableLiveData<String>()
    val searchText: LiveData<String> = _searchText

    fun onPageStarted() {
        isLoading.set(true)
    }

    fun onPageFinished() {
        isLoading.set(false)
        val text = ReadFragmentArgs.fromSavedStateHandle(savedStateHandle).searchResult
        if (text.isNotEmpty()) _searchText.value = text
    }
}