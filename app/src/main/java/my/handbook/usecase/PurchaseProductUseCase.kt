package my.handbook.usecase

import android.app.Activity
import my.handbook.data.remote.PlayBillingDataSource
import my.handbook.data.remote.PlayBillingResponse
import javax.inject.Inject

class PurchaseProductUseCase @Inject constructor(
    private val playBillingDataSource: PlayBillingDataSource,
) {
    suspend fun execute(activity: Activity, productId: String): UseCaseResult<List<String>> {
        return when (val response = playBillingDataSource.purchaseProduct(activity, productId)) {
            is PlayBillingResponse.Success -> UseCaseResult.Success(response.data)
            is PlayBillingResponse.Error -> UseCaseResult.Error(response.message)
        }
    }
}
