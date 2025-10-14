package com.capacitor.plugin.storefront

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingConfig
import com.android.billingclient.api.BillingConfigResponseListener
import com.android.billingclient.api.BillingResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CapacitorStorefrontTest {

    private lateinit var capacitorStorefront: CapacitorStorefront
    private lateinit var context: Context
    private lateinit var billingClient: BillingClient
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        context = mockk(relaxed = true)
        billingClient = mockk(relaxed = true)
        capacitorStorefront = CapacitorStorefront()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `initial status should be NOT_INITIALIZED`() {
        assertEquals(Status.NOT_INITIALIZED, capacitorStorefront.getStatus())
    }

    @Test
    fun `initialize - successful case`() = runTest {
        // Arrange
        val mockBillingResult = mockk<BillingResult> {
            every { responseCode } returns BillingClient.BillingResponseCode.OK
            every { debugMessage } returns "Success"
        }

        mockkStatic(BillingClient::class)
        every { BillingClient.newBuilder(any()) } returns mockk(relaxed = true) {
            every { setListener(any()) } returns this
            every { enablePendingPurchases() } returns this
            every { build() } returns billingClient
        }

        val listenerSlot = slot<BillingClientStateListener>()
        every { billingClient.startConnection(capture(listenerSlot)) } answers {
            listenerSlot.captured.onBillingSetupFinished(mockBillingResult)
        }

        // Act
        capacitorStorefront.initialize(context)

        // Assert
        assertEquals(Status.INITIALIZED, capacitorStorefront.getStatus())
        verify {
            billingClient.startConnection(any())
        }
    }


    @Test
    fun `initialize - failure case`() = runTest {
        // Arrange
        val mockBillingResult = mockk<BillingResult> {
            every { responseCode } returns BillingClient.BillingResponseCode.ERROR
            every { debugMessage } returns "Error"
        }

        mockkStatic(BillingClient::class)
        every { BillingClient.newBuilder(any()) } returns mockk(relaxed = true) {
            every { setListener(any()) } returns this
            every { enablePendingPurchases() } returns this
            every { build() } returns billingClient
        }

        val listenerSlot = slot<BillingClientStateListener>()
        every { billingClient.startConnection(capture(listenerSlot)) } answers {
            listenerSlot.captured.onBillingSetupFinished(mockBillingResult)
        }

        // Act & Assert
        // Use assertFailsWith for suspend functions
        assertFailsWith<IllegalStateException> {
            capacitorStorefront.initialize(context)
        }

        // Because the exception is caught, the status remains NOT_INITIALIZED.
        assertEquals(Status.NOT_INITIALIZED, capacitorStorefront.getStatus())

        // Verification will now pass as startConnection is called before the exception is thrown.
        verify {
            billingClient.startConnection(any())
        }
    }

    @Test
    fun `getStorefront - successful case`() = runTest {
        // Arrange
        val expectedCountryCode = "US"
        val mockBillingConfig = mockk<BillingConfig> {
            every { countryCode } returns expectedCountryCode
        }
        val mockBillingResult = mockk<BillingResult> {
            every { responseCode } returns BillingClient.BillingResponseCode.OK
        }

        // Initialize first
        mockkStatic(BillingClient::class)
        every { BillingClient.newBuilder(any()) } returns mockk(relaxed = true) {
            every { setListener(any()) } returns this
            every { enablePendingPurchases() } returns this
            every { build() } returns billingClient
        }

        val listenerSlot = slot<BillingClientStateListener>()
        every { billingClient.startConnection(capture(listenerSlot)) } answers {
            listenerSlot.captured.onBillingSetupFinished(mockk {
                every { responseCode } returns BillingClient.BillingResponseCode.OK
            })
        }

        every { billingClient.isReady } returns true

        val configListenerSlot = slot<BillingConfigResponseListener>()
        every {
            billingClient.getBillingConfigAsync(any(), capture(configListenerSlot))
        } answers {
            configListenerSlot.captured.onBillingConfigResponse(mockBillingResult, mockBillingConfig)
        }

        // Act
        capacitorStorefront.initialize(context)
        val result = capacitorStorefront.getStorefront()

        // Assert
        assertEquals(expectedCountryCode, result)
        verify {
            billingClient.getBillingConfigAsync(any(), any())
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `getStorefront - not initialized should throw`() = runTest {
        capacitorStorefront.getStorefront()
    }

    @Test
    fun `getStorefront - failure case`() = runTest {
        // Arrange
        val mockBillingResult = mockk<BillingResult> {
            every { responseCode } returns BillingClient.BillingResponseCode.ERROR
            every { debugMessage } returns "Error getting billing config"
        }

        // Initialize first
        mockkStatic(BillingClient::class)
        every { BillingClient.newBuilder(any()) } returns mockk(relaxed = true) {
            every { setListener(any()) } returns this
            every { enablePendingPurchases() } returns this
            every { build() } returns billingClient
        }

        val listenerSlot = slot<BillingClientStateListener>()
        every { billingClient.startConnection(capture(listenerSlot)) } answers {
            listenerSlot.captured.onBillingSetupFinished(mockk {
                every { responseCode } returns BillingClient.BillingResponseCode.OK
            })
        }

        every { billingClient.isReady } returns true

        val configListenerSlot = slot<BillingConfigResponseListener>()
        every {
            billingClient.getBillingConfigAsync(any(), capture(configListenerSlot))
        } answers {
            configListenerSlot.captured.onBillingConfigResponse(mockBillingResult, null)
        }

        // Act & Assert
        capacitorStorefront.initialize(context)

        // Use assertFailsWith and remove the nested runTest
        assertFailsWith<IllegalStateException> {
            capacitorStorefront.getStorefront()
        }
    }
    @Test
    fun `service disconnected should update status`() = runTest {
        // Arrange
        mockkStatic(BillingClient::class)
        every { BillingClient.newBuilder(any()) } returns mockk(relaxed = true) {
            every { setListener(any()) } returns this
            every { enablePendingPurchases() } returns this
            every { build() } returns billingClient
        }

        val listenerSlot = slot<BillingClientStateListener>()
        every { billingClient.startConnection(capture(listenerSlot)) } answers {
            listenerSlot.captured.onBillingSetupFinished(mockk {
                every { responseCode } returns BillingClient.BillingResponseCode.OK
            })
        }

        // Initialize first
        capacitorStorefront.initialize(context)
        assertEquals(Status.INITIALIZED, capacitorStorefront.getStatus())

        // Act
        listenerSlot.captured.onBillingServiceDisconnected()

        // Assert
        assertEquals(Status.NOT_INITIALIZED, capacitorStorefront.getStatus())
    }
}