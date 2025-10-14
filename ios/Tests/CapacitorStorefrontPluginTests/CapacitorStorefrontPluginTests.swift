import XCTest
@testable import CapacitorStorefrontPlugin

class CapacitorStorefrontTests: XCTestCase {
	var storefront: CapacitorStorefront!

	override func setUp() {
		super.setUp()
		storefront = CapacitorStorefront()
	}

	override func tearDown() {
		storefront = nil
		super.tearDown()
	}

	func test_getStorefront_throws_when_not_initialized() {
		// When not initialized, getStorefront should throw an error
		XCTAssertThrowsError(try storefront.getStorefront()) { error in
			// Verify the error domain/message is the one from the implementation
			let nsError = error as NSError
			XCTAssertEqual(nsError.domain, "CapacitorStorefront")
		}
	}

	func test_initialize_and_deinitialize_lifecycle() {
		// Initialize should succeed (no throw)
		XCTAssertNoThrow(try storefront.initialize())

		// Calling initialize again should throw "Already initialized"
		XCTAssertThrowsError(try storefront.initialize()) { error in
			let nsError = error as NSError
			XCTAssertEqual(nsError.domain, "CapacitorStorefront")
			XCTAssertEqual(nsError.code, -1)
		}

		// After initialization, getStorefront may throw if StoreKit isn't available; at minimum
		// calling deinitialize should succeed
		XCTAssertNoThrow(try storefront.deinitialize())

		// After deinitialize, getStorefront should again throw for not initialized
		XCTAssertThrowsError(try storefront.getStorefront())
	}
}
