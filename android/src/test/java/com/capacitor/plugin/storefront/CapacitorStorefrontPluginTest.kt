package com.capacitor.plugin.storefront

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.getcapacitor.JSObject
import com.getcapacitor.PluginCall
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CapacitorStorefrontPluginTest {

    private lateinit var plugin: CapacitorStorefrontPlugin
    private lateinit var mockImpl: CapacitorStorefront
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        mockImpl = mockk<CapacitorStorefront>()
        plugin = CapacitorStorefrontPlugin(mockImpl)
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `initialize resolves when implementation succeeds`() {
        val call = mockk<PluginCall>(relaxed = true)
        try {
        coEvery { mockImpl.initialize(any()) } just Runs
        } catch (e: Exception) {
            // Handle exception if needed
            e.printStackTrace()
        }

        plugin.initialize(call)

        // advance coroutines to completion
        testDispatcher.scheduler.advanceUntilIdle()

        verify { call.resolve() }
    }

    @Test
    fun `initialize rejects when implementation throws`() {
        val call = mockk<PluginCall>(relaxed = true)

        val mockImpl = mockk<CapacitorStorefront>()
        coEvery { mockImpl.initialize(any()) } throws RuntimeException("init failed")
        val field = plugin.javaClass.getDeclaredField("implementation")
        field.isAccessible = true
        field.set(plugin, mockImpl)

        plugin.initialize(call)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { call.reject(match { it.contains("init failed") }) }
    }

    @Test
    fun `getStorefront resolves with countryCode when implementation succeeds`() {
        val call = mockk<PluginCall>(relaxed = true)
        val expected = "US"

        val mockImpl = mockk<CapacitorStorefront>()
        coEvery { mockImpl.getStorefront() } returns expected
        val field = plugin.javaClass.getDeclaredField("implementation")
        field.isAccessible = true
        field.set(plugin, mockImpl)

        plugin.getStorefront(call)
        testDispatcher.scheduler.advanceUntilIdle()

        verify {
            call.resolve(match { obj: JSObject -> obj.getString("countryCode") == expected })
        }
    }

    @Test
    fun `getStorefront rejects when implementation throws`() {
        val call = mockk<PluginCall>(relaxed = true)

        val mockImpl = mockk<CapacitorStorefront>()
        coEvery { mockImpl.getStorefront() } throws RuntimeException("no config")
        val field = plugin.javaClass.getDeclaredField("implementation")
        field.isAccessible = true
        field.set(plugin, mockImpl)

        plugin.getStorefront(call)
        testDispatcher.scheduler.advanceUntilIdle()

        verify { call.reject(match { it.contains("no config") }) }
    }
}
