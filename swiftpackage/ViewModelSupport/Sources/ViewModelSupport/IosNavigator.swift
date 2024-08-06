//
//  IosNavigator.swift
//
//
//  Created by Mike Roberts on 2024-08-03.
//

import UIKit

fileprivate class ViewControllerTarget<ViewControllerType: UIViewController> {
}

public class IosNavigator<NavigationControllerType: UINavigationController> {
    private weak var viewController: UIViewController?

    private var navigationController: UINavigationController? {
        get {
            guard let vc = self.viewController else {
                return nil
            }

            if vc is UINavigationController {
                return vc as? UINavigationController
            }

            if vc.navigationController != nil {
                return vc.navigationController
            }

            if vc.presentingViewController != nil {
                return vc.presentingViewController as? UINavigationController
            }

            if vc.tabBarController != nil {
                return vc.tabBarController?.navigationController
            }

            return nil
        }
    }

    init(viewController: UIViewController) {
        self.viewController = viewController
    }

    func close() {
        runOnMain {
            if (self.viewController?.presentingViewController != nil) {
                self.viewController?.dismiss(animated: true)
            } else {
                self.viewController?.navigationController?.popViewController(animated: true)
            }
        }
    }

    private func pushViewController<
        ViewControllerTargetType: ViewControllerTarget<ViewControllerType>,
        ViewControllerType: ArgViewController
    >(target: ViewControllerTargetType, args: ViewControllerType.ArgType, intoRoot: Bool = false) {
        runOnMain { [args/*, resolver*/] in
            let targetViewController = ViewControllerType.init(args: args)//, resolver: resolver)

            if intoRoot {
                var navigationController = self.navigationController

                while navigationController?.presentingViewController != nil {
                    let presenting = navigationController?.presentingViewController
                    let oldNavigationController = navigationController

                    if presenting is UINavigationController {
                        navigationController = presenting as? UINavigationController
                    } else if presenting?.navigationController != nil {
                        navigationController = presenting?.navigationController
                    }

                    if oldNavigationController != navigationController {
                        oldNavigationController?.dismiss(animated: true)
                    } else {
                        break
                    }
                }
                navigationController?.pushViewController(targetViewController, animated: true)
            } else {
                self.navigationController?.pushViewController(targetViewController, animated: true)
            }
        }
    }

    private func pushViewController<
        ViewControllerRefType: ViewControllerTarget<OverViewControllerType>,
        OverViewControllerType: UIViewController,
        ViewControllerTargetType: ViewControllerTarget<ViewControllerType>,
        ViewControllerType: ArgViewController
    >(target: ViewControllerTargetType, over: ViewControllerRefType, args: ViewControllerType.ArgType, after: @escaping (OverViewControllerType) -> Void = { _ in }) {
        runOnMain { [args/*, resolver*/] in
            var toDismiss: [UIViewController] = []
            var overTargetVc: OverViewControllerType!
            let targetViewController = ViewControllerType.init(args: args)//, resolver: resolver)

            guard self.navigationController != nil else {
                return
            }

            while true {
                var found = false
                var subset: [UIViewController] = []

                guard let targetNavController = self.navigationController else {
                    return
                }
                for vc in targetNavController.viewControllers {
                    subset.append(vc)

                    if vc is OverViewControllerType {
                        overTargetVc = vc as? OverViewControllerType
                        found = true
                        break
                    }
                }

                if !found {
                    if self.viewController?.parent is NavigationControllerType && self.viewController?.parent?.presentingViewController != nil {

                        toDismiss.append((self.viewController?.parent)!)

                        self.viewController = self.viewController?.parent?.presentingViewController
                    }

                    continue
                }

                let override = targetNavController.viewControllers.count > subset.count || toDismiss.count > 0

                targetNavController.pushViewController(targetViewController, animated: true)

                subset.append(targetViewController)

                if override {
                    let waitingForDismiss = toDismiss.count > 0

                    // this moves the pushed VC onto the root navigation after
                    // it is presented within the modal view controller
                    // this lets you move up and down the stack without
                    // the user noticingte
                    for vc in toDismiss {
                        if let navVc = vc as? NavigationControllerType {
//                            navVc.beforeTransitionWork = {
//                                runOnMainLater {
//                                    vc.dismiss(animated: false)
//                                }
//                            }
//                            navVc.afterWork = {
//                                targetNavController.viewControllers = subset
//                                after(overTargetVc)
//                            }
                            runOnMainAfter(delay: 0.3) {
                                targetNavController.viewControllers = subset
                                after(overTargetVc)
                            }
                        } else {
                            vc.dismiss(animated: false)
                        }
                    }

                    if !waitingForDismiss {
                        targetNavController.viewControllers = subset
                        after(overTargetVc)
                    }
                }

                break
            }
        }
    }

    private func pushRootViewController<
        ViewControllerTargetType: ViewControllerTarget<ViewControllerType>,
        ViewControllerType: ArgViewController
    >(target: ViewControllerTargetType, args: ViewControllerType.ArgType) {
        runOnMain { [args/*, resolver*/] in
            let targetViewController = ViewControllerType.init(args: args)//, resolver: resolver)
            let bottomVc = self.navigationController?.viewControllers[0]

            var navigationController = self.navigationController
            while navigationController?.presentingViewController != nil {
                let presenting = navigationController?.presentingViewController
                let oldNavigationController = navigationController

                if presenting is UINavigationController {
                    navigationController = presenting as? UINavigationController
                } else if presenting?.navigationController != nil {
                    navigationController = presenting?.navigationController
                }

                if oldNavigationController == navigationController {
                    break
                }
            }

            if !navigationController!.viewControllers.isEmpty {
                navigationController?.viewControllers[0].dismiss(animated: true) {
                    navigationController?.pushViewController(targetViewController, animated: true)
                    navigationController?.viewControllers = [targetViewController]
                }
            } else {
                navigationController?.pushViewController(targetViewController, animated: true)
                navigationController?.viewControllers = [targetViewController]
            }
        }
    }

    private func pushModalViewController<
        ViewControllerTargetType: ViewControllerTarget<ViewControllerType>,
        ViewControllerType: ArgViewController
    >(target: ViewControllerTargetType, args: ViewControllerType.ArgType) {
        runOnMain { [args/*, resolver*/] in
            let targetViewController = ViewControllerType.init(args: args)//, resolver: resolver)
            let rootNav = NavigationControllerType(rootViewController: targetViewController)

            rootNav.modalPresentationStyle = targetViewController.modalPresentationStyle

            self.navigationController?.topViewController?.present(rootNav, animated: true)
        }
    }

    private func popupViewController(_ targetViewController: UIViewController) {
        let rootNav = NavigationControllerType(rootViewController: targetViewController)

        self.navigationController?.topViewController?.present(rootNav, animated: true)
    }
}
