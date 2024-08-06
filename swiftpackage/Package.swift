// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "ViewModelSupport",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "ViewModelSupport",
            targets: ["ViewModelSupport"]
        )
    ],
    targets: [
        .target(
            name: "ViewModelSupport",
            path: "ViewModelSupport"
        )
    ],
    swiftLanguageVersions: [.v5]
)
