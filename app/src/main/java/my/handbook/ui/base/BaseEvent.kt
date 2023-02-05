package my.handbook.ui.base

import androidx.navigation.NavDirections

sealed class BaseEvent {
    object NavigateBack : BaseEvent()
    data class NavigateTo(val direction: NavDirections) : BaseEvent()
    data class OpenWebPage(val address: String) : BaseEvent()
    data class ShowTextMessage(val message: String) : BaseEvent()
}
