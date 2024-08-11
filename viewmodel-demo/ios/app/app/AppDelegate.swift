//
//  AppDelegate.swift
//  app
//
//  Created by Mike Roberts on 2024-08-02.
//

import UIKit
import Core
import ViewModelSupport

let appStartupTime = ProcessInfo.processInfo.systemUptime

extension CoreNavigableViewController {
    var core: Core {
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        
        return appDelegate.core;
    }
}

class AppNavigator: IosNavigator<UINavigationController>, Navigator {
    
    func navigateTo(home: IHomeViewModelArgs) {
        self.pushRootViewController(
            target: ViewControllerTarget<HomeViewController>(),
            args: home
        )
    }
}

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    var core: Core!

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let window = UIWindow(frame: UIScreen.main.bounds)

        self.window = window

        window.backgroundColor = .clear
        
        self.core = Core(serverPath: "latenighthack.com")
        
        let navigationController = UINavigationController()
        let navigator = AppNavigator(viewController: navigationController)
        
        window.rootViewController = navigationController
        window.makeKeyAndVisible()
        
        StartupKt.startup(core: self.core, navigator: navigator) {
        }

        return true
    }
}
