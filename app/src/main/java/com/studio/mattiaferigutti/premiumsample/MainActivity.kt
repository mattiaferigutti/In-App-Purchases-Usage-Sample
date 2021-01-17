package com.studio.mattiaferigutti.premiumsample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {

    var billingProcessor: BillingProcessor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpBillingProcessor()

        button.setOnClickListener {
            purchaseProduct()
        }

        if (isPremiumUser()) {
            updateForPremiumUsers()
        }
    }

    /**
     * @return true if the product has been purchased. Otherwise it returns false.
     */
    private fun isPremiumUser() : Boolean {
        return billingProcessor?.isPurchased(PRODUCT_ID) == true
    }

    private fun purchaseProduct() {
        val isOneTimePurchaseSupported = billingProcessor?.isOneTimePurchaseSupported
        if (isOneTimePurchaseSupported == true) {
            billingProcessor?.purchase(this, PRODUCT_ID)
        }
    }

    private fun updateForPremiumUsers() {
        text.text = "You have a premium version!"
    }

    private fun setUpBillingProcessor() {
        billingProcessor = BillingProcessor(this, LICENSE_KEY, this)
        billingProcessor?.initialize()
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        Log.v(TAG, "$productId was successfully purchased")
        /*
        * Called when requested PRODUCT ID was successfully purchased
        */

        if (productId == PRODUCT_ID) {
            Log.v(TAG, "right id code")
            updateForPremiumUsers()
        }
    }

    override fun onPurchaseHistoryRestored() {
        Log.v(TAG, "onPurchaseHistoryRestored")
        /*
        * Called when purchase history was restored and the list of all owned PRODUCT ID's
        * was loaded from Google Play
        */

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.v(TAG, "onBillingError")
        /*
        * Called when some error occurred. See Constants class for more details
        *
        * Note - this includes handling the case where the user canceled the buy dialog:
        * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
        */

    }

    override fun onBillingInitialized() {
        Log.v(TAG, "onBillingInitialized")
        /*
        * Called when BillingProcessor was initialized and it's ready to purchase
        */

        billingProcessor?.getPurchaseListingDetails(PRODUCT_ID)
        if (billingProcessor?.isPurchased(PRODUCT_ID) == true) {
            Log.v("TAG_IN_APP", "PRODUCT ID was successfully purchased")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!billingProcessor?.handleActivityResult(requestCode, resultCode, data)!!) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingProcessor?.release()
    }

    companion object {
        val TAG = MainActivity::class.java.name + ".TAG_IN_APP"
        const val PRODUCT_ID = "pro_version_test_1"
        const val LICENSE_KEY = "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE"
    }
}