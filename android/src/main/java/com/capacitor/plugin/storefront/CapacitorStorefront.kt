package com.capacitor.plugin.storefront

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.GetBillingConfigParams
import com.android.billingclient.api.PurchasesUpdatedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Manages the initialization of the BillingClient and the retrieval of product configurations.
 * This class contains the business logic.
 */
class CapacitorStorefront() {

    private var billingClient: BillingClient? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var initializationStatus = Status.NOT_INITIALIZED

    // Listener required by the BillingClient to handle purchase updates.
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        // The logic for handling purchases should be placed here.
    }

    // Getter method for the status
    fun getStatus(): Status = initializationStatus

    /**
     * Initializes the BillingClient and attempts to connect to Google Play asynchronously.
     * @return A pair containing the response code and a debug message.
     */
    suspend fun initialize(context: Context) = suspendCancellableCoroutine<Unit> { continuation ->
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        // 2. Asynchronous connection (Future-like)
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    initializationStatus = Status.INITIALIZED
                    continuation.resume(Unit)
                } else {
                    initializationStatus = Status.NOT_INITIALIZED
                    continuation.resumeWithException(
                        IllegalStateException("Billing Client setup failed: ${billingResult.debugMessage}")
                    )
                }
            }

            override fun onBillingServiceDisconnected() {
                initializationStatus = Status.NOT_INITIALIZED
                // Automatic reconnection attempt managed internally by the BillingClient in theory
            }
        })
    }

    /**
     * Retrieves product configurations (billing config) from the Google Play Store.
     * The operation is completely asynchronous.
     * @return A list of ProductDetails.
     */
    suspend fun getStorefront(): String = suspendCancellableCoroutine { continuation ->
        if (initializationStatus != Status.INITIALIZED || billingClient?.isReady == false) {
            continuation.resumeWithException(IllegalStateException("Billing Client not initialized."))
            return@suspendCancellableCoroutine
        }

        billingClient?.getBillingConfigAsync(
            GetBillingConfigParams.newBuilder().build(),
            { billingResult, billingConfig ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && billingConfig != null) {
                    val countryCode = billingConfig.countryCode
                    if (countryCode.isNotEmpty()) {
                        continuation.resume(countryCode)
                    } else {
                        continuation.resumeWithException(IllegalStateException("Country code not available"))
                    }
                } else {
                    continuation.resumeWithException(IllegalStateException("Failed to load billing config: ${billingResult.debugMessage}"))
                }
            }
        )
    }

    /**
     * Call this method when the plugin is destroyed to release resources.
     */
    fun destroy() {
        billingClient?.endConnection()
        scope.cancel()
    }
}