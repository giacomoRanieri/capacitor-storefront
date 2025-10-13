import Foundation

@objc public class CapacitorStorefront: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
