import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorStorefrontPlugin)
public class CapacitorStorefrontPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "CapacitorStorefrontPlugin"
    public let jsName = "CapacitorStorefront"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "initialize", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getStorefront", returnType: CAPPluginReturnPromise)
    ]

    private let storefront = CapacitorStorefront()
    
    @objc func initialize(_ call: CAPPluginCall) {
        // try catch error and reject the call if initialization fails
        do {
            try storefront.initialize()
            call.resolve()
        } catch {
            call.reject("Failed to initialize storefront: \(error.localizedDescription)")
        }
    }

    @objc func getStorefront(_ call: CAPPluginCall) {
         // try catch error and reject the call if not initialized
        do {
            let countryCode = try storefront.getStorefront()
            call.resolve(["countryCode": countryCode])
        } catch {
            call.reject("Failed to get storefront: \(error.localizedDescription)")
        }
    }
}
