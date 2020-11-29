package my.handbook.billing.ui

import androidx.hilt.lifecycle.ViewModelInject
import my.handbook.billing.core.BillingHandler

class SimpleBillingViewModel @ViewModelInject constructor(
    billingHandler: BillingHandler
) : BillingViewModel(billingHandler)
