// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "ViewModelCore",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "ViewModelCore",
            targets: ["ViewModelCore"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "ViewModelCore",
            path: "./ViewModelCore.xcframework"
        ),
    ]
)
