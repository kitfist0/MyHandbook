package my.handbook.data.remote

sealed class PurchasedIdsResponse {
    data class Success(val purchasedIds: List<String>) : PurchasedIdsResponse()
    data class Error(val message: String) : PurchasedIdsResponse()
}

inline fun PurchasedIdsResponse.onSuccess(block: (List<String>) -> Unit) {
    if (this is PurchasedIdsResponse.Success) {
        block.invoke(purchasedIds)
    }
}
