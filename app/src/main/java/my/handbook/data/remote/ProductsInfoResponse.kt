package my.handbook.data.remote

sealed class ProductsInfoResponse {
    data class Success(val productsInfo: List<ProductInfo>) : ProductsInfoResponse()
    data class Error(val message: String) : ProductsInfoResponse()
}

inline fun ProductsInfoResponse.onSuccess(block: (List<ProductInfo>) -> Unit) {
    if (this is ProductsInfoResponse.Success) {
        block.invoke(productsInfo)
    }
}
