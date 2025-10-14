package com.capacitor.plugin.storefront

import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob

@CapacitorPlugin(name = "CapacitorStorefront")
class CapacitorStorefrontPlugin(private val implementation: CapacitorStorefront = CapacitorStorefront()) : Plugin() {
    companion object {
        private const val TAG = "CapacitorStorefront"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    fun initialize(call: PluginCall) {
        scope.launch {
            try {
                implementation.initialize(context)
                call.resolve()
            } catch (e: Exception) {
                Logger.error(TAG, "Error during initialization", e)
                call.reject(e.message ?: "Initialization failed")
            }
        }
    }

    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    fun getStorefront(call: PluginCall) {
        scope.launch {
            try {
                val countryCode = implementation.getStorefront()
                val result = JSObject().apply {
                    put("countryCode", countryCode)
                }
                call.resolve(result)
            } catch (e: Exception) {
                Logger.error(TAG, "Error retrieving storefront", e)
                call.reject(e.message ?: "Failed to get storefront")
            }
        }
    }
}
