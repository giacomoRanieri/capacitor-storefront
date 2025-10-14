import Foundation
import StoreKit

@objc public class CapacitorStorefront: NSObject {

    private enum Status {
        case uninitialized
        case initialized
    }
    
    private enum StorefrontError: Error {
        case alreadyInitialized
        case notInitialized
        case storefrontNotAvailable
    }

    private var status: Status = .uninitialized
    private var skPaymentQueue: SKPaymentQueue?

    @objc public func initialize() throws {
        if status != .uninitialized {
            throw StorefrontError.alreadyInitialized
        }
        skPaymentQueue = SKPaymentQueue.default()
        status = .initialized
    }

    @objc public func getStorefront() throws-> String {
        if status == .uninitialized {
            throw StorefrontError.notInitialized
        }

        guard let queue = skPaymentQueue,
              let countryCode = queue.storefront?.countryCode else {
            throw StorefrontError.storefrontNotAvailable
        }
        
        return countryCode
    }

    @objc public func getStatus() -> String {
        switch status {
        case .uninitialized:
            return "uninitialized"
        case .initialized:
            return "initialized"
        }
    }

    @objc public func deinitialize() throws {
        if status == .uninitialized {
            throw StorefrontError.notInitialized
        }
        skPaymentQueue = nil
        status = .uninitialized
    }
}
