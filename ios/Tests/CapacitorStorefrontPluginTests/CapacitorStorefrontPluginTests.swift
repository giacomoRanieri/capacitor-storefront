import XCTest
@testable import CapacitorStorefrontPlugin

class CapacitorStorefrontTests: XCTestCase {
    var storefront: CapacitorStorefront!

    override func setUp() {
        super.setUp()
        storefront = CapacitorStorefront()
    }

    override func tearDown() {
        try? storefront.deinitialize()
        storefront = nil
        super.tearDown()
    }

    func test_getStorefront_throws_when_not_initialized() throws {
        do {
            _ = try storefront.getStorefront()
            XCTFail("Expected getStorefront to throw error")
        } catch let error as StorefrontError {
            XCTAssertEqual(error, StorefrontError.notInitialized)
        }
    }

    func test_initialize_and_deinitialize() throws {
        // First initialization should succeed
        XCTAssertNoThrow(try storefront.initialize())

        // Second initialization should throw alreadyInitialized
        do {
            try storefront.initialize()
            XCTFail("Expected second initialize to throw error")
        } catch let error as StorefrontError {
            XCTAssertEqual(error, StorefrontError.alreadyInitialized)
        }

        // Deinitialize should succeed
        XCTAssertNoThrow(try storefront.deinitialize())

        // After deinitialize, getStorefront should throw notInitialized
        do {
            _ = try storefront.getStorefront()
            XCTFail("Expected getStorefront to throw error after deinitialize")
        } catch let error as StorefrontError {
            XCTAssertEqual(error, StorefrontError.notInitialized)
        }
    }

    func test_deinitialize_throws_when_not_initialized() {
        XCTAssertThrowsError(try storefront.deinitialize()) { error in
            guard let storefrontError = error as? StorefrontError else {
                XCTFail("Expected StorefrontError")
                return
            }
            XCTAssertEqual(storefrontError, StorefrontError.notInitialized)
        }
    }

	    func test_getStatus_returns_correct_state() throws {
        // Initial state should be uninitialized
        XCTAssertEqual(storefront.getStatus(), "uninitialized")
        
        // After initialization, should be initialized
        try storefront.initialize()
        XCTAssertEqual(storefront.getStatus(), "initialized")
        
        // After deinitialization, should be uninitialized again
        try storefront.deinitialize()
        XCTAssertEqual(storefront.getStatus(), "uninitialized")
    }
}
