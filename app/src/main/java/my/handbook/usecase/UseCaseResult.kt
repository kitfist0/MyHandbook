package my.handbook.usecase

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class UseCaseResult<out R> {
    data class Success<out T>(val data: T) : UseCaseResult<T>()
    object Loading : UseCaseResult<Nothing>()
}

/**
 * Updates value of [MutableStateFlow] if [Result] is of type [UseCaseResult.Success]
 */
inline fun <reified T> UseCaseResult<T>.updateOnSuccess(stateFlow: MutableStateFlow<T>) {
    if (this is UseCaseResult.Success) {
        stateFlow.value = data
    }
}
