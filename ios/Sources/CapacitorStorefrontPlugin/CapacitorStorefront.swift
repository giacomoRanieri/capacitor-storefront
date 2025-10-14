import Foundation
import StoreKit

@objc public class CapacitorStorefront: NSObject {

    private var status: Status = .uninitialized
    private var skPaymentQueue: SKPaymentQueue?

    @objc public func initialize() -> Void {
        if status != .uninitialized {
            throw NSError(domain: "CapacitorStorefront", code: -1, userInfo: [NSLocalizedDescriptionKey: "Already initialized"])
        }
        skPaymentQueue = SKPaymentQueue.default()
        status = .initialized
    }

    @objc public func getStorefront() -> String {
        if status == .uninitialized {
            throw NSError(domain: "CapacitorStorefront", code: -1, userInfo: [NSLocalizedDescriptionKey: "Not initialized"])
        }
        return skPaymentQueue?.storefront.countryCode
    }

    @objc public func deinitialize() -> Void {
        if status == .uninitialized {
            throw NSError(domain: "CapacitorStorefront", code: -1, userInfo: [NSLocalizedDescriptionKey: "Not initialized"])
        }
        skPaymentQueue = nil
        status = .uninitialized
    }
}
