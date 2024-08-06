//
//  AppDelegate.swift
//  app
//
//  Created by Mike Roberts on 2024-08-02.
//

import UIKit
import Core

let appStartupTime = ProcessInfo.processInfo.systemUptime

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let window = UIWindow(frame: UIScreen.main.bounds)

        self.window = window

        window.backgroundColor = .clear
        
        let viewController = HomeViewController(args: IHomeViewModelArgs())
        let navigationController = UINavigationController(rootViewController: viewController)

        window.rootViewController = navigationController
        window.makeKeyAndVisible()

        return true
    }
}
