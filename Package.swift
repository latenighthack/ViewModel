// swift-tools-version: 5.10
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "ViewModelSupport",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        // Products define the executables and libraries a package produces, making them visible to other packages.
        .library(
            name: "ViewModelSupport",
            targets: ["ViewModelSupport"]),
        
    ],
    dependencies: [
        .package(name: "ViewModelCore", url: "https://latenighthack.com/ViewModelCore")
    ],
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .target(
            name: "ViewModelSupport",
            dependencies: [
                .product(name: "ViewModelCore", package: "ViewModelCore")
            ]),
        .testTarget(
            name: "ViewModelSupportTests",
            dependencies: ["ViewModelSupport"]),
    ]
)
