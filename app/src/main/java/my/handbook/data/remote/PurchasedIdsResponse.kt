package my.handbook.data.remote

sealed class PurchasedIdsResponse {
    data class Success(val purchasedIds: List<String>) : PurchasedIdsResponse()
    data class Error(val message: String) : PurchasedIdsResponse()
}
