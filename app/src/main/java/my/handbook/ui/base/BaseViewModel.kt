package my.handbook.ui.base

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    val isEmpty = ObservableBoolean(false)
    val isLoading = ObservableBoolean(false)

    private val eventChannel = Channel<BaseEvent>()
    val baseEvents = eventChannel.receiveAsFlow()

    open fun onBackPressed() {
        navigateBack()
    }

    protected fun navigateTo(direction: NavDirections) {
        offerEvent(BaseEvent.NavigateTo(direction))
    }

    protected fun navigateBack() {
        offerEvent(BaseEvent.NavigateBack)
    }

    protected fun openWebPage(address: String) {
        offerEvent(BaseEvent.OpenWebPage(address))
    }

    protected fun showTextMessage(message: String) {
        offerEvent(BaseEvent.ShowTextMessage(message))
    }

    private fun offerEvent(event: BaseEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}
