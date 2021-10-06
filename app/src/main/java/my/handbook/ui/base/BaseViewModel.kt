package my.handbook.ui.base

import android.content.Intent
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    val isLoading = ObservableBoolean(false)

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    fun navigateTo(direction: NavDirections) {
        offerEvent(Navigate(direction))
    }

    fun navigateUp() {
        offerEvent(NavigateUp)
    }

    fun navigateBack() {
        offerEvent(NavigateBack)
    }

    fun showMessage(message: String) {
        offerEvent(TextMessage(message))
    }

    fun startActivity(intent: Intent) {
        offerEvent(StartActivity(intent))
    }

    open fun onBackPressed() {
        navigateBack()
    }

    private fun offerEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }
}
