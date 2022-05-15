package simple.billing.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BillingAppCompatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BillingHandler.getInstance(this).initBilling(lifecycle)
    }
}
