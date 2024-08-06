// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "ViewModelSupport",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "ViewModelSupportCore",
            targets: ["ViewModelSupportCore"]
        ),
        .library(
            name: "ViewModelSupport",
            targets: ["ViewModelSupport"]
        )
    ],
    targets: [
        .target(
            name: "ViewModelSupportCoreObjC",
            path: "ViewModelSupportCoreObjC",
            publicHeadersPath: "."
        ),
        .target(
            name: "ViewModelSupportCore",
            dependencies: [.target(name: "ViewModelSupportCoreObjC")],
            path: "ViewModelSupportCoreObjC"
        ),
        .target(
            name: "ViewModelSupport",
            dependencies: [.target(name: "ViewModelSupport")],
            path: "ViewModelSupport"
        )
    ],
    swiftLanguageVersions: [.v5]
)
