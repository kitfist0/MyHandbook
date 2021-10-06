package my.handbook.ui.base

import android.content.Intent
import androidx.navigation.NavDirections

interface Event

data class TextMessage(val message: String) : Event

data class StartActivity(val intent: Intent) : Event

sealed class NavigationEvent : Event

data class Navigate(val direction: NavDirections) : NavigationEvent()

object NavigateUp : NavigationEvent()

object NavigateBack : NavigationEvent()
