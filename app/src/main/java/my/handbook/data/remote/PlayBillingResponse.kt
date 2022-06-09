package my.handbook.data.remote

sealed class PlayBillingResponse<out R> {
    data class Success<out T>(val data: T) : PlayBillingResponse<T>()
    data class Error(val message: String) : PlayBillingResponse<Nothing>()
}

inline fun <reified T> PlayBillingResponse<T>.onSuccess(block: (T) -> Unit) {
    if (this is PlayBillingResponse.Success) {
        block.invoke(data)
    }
}
