// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorStorefront",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorStorefront",
            targets: ["CapacitorStorefrontPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "CapacitorStorefrontPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/CapacitorStorefrontPlugin"),
        .testTarget(
            name: "CapacitorStorefrontPluginTests",
            dependencies: ["CapacitorStorefrontPlugin"],
            path: "ios/Tests/CapacitorStorefrontPluginTests")
    ]
)