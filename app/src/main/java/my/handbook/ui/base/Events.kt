package my.handbook.ui.base

import androidx.navigation.NavDirections

interface Event

data class TextMessage(val message: String) : Event

sealed class NavigationEvent : Event

data class Navigate(val direction: NavDirections) : NavigationEvent()

object NavigateUp : NavigationEvent()

object NavigateBack : NavigationEvent()
