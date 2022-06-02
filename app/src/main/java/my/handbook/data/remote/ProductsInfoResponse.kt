package my.handbook.data.remote

sealed class ProductsInfoResponse {
    data class Success(val productsInfo: List<ProductInfo>) : ProductsInfoResponse()
    data class Error(val message: String) : ProductsInfoResponse()
}
