//
//  UIKit+Binding.swift
//
//
//  Created by Mike Roberts on 2024-08-09.
//

import UIKit

public protocol Spinnerable: AnyObject {
    func showSpinner()
    func hideSpinner()
    func showErrorDialog(error: Error)
}

public extension UIViewController {
    
    func performAsyncAction(withSpinner: Bool = true, _ action: (@escaping (Error?) -> Void) -> Void) {
        var shouldShowSpinner = withSpinner
        var spinnerShown = false
        
        action { [weak self] error in
            shouldShowSpinner = false
            
            guard let self = self else {
                return
            }
            
            runOnMain {
                if spinnerShown {
                    if self.responds(to: #selector(Spinnerable.hideSpinner)) {
                        self.perform(#selector(Spinnerable.hideSpinner))
                    }
                }
                
                if let err = error {
                    if self.responds(to: #selector(Spinnerable.showErrorDialog)) {
                        self.perform(#selector(Spinnerable.showErrorDialog), with: err)
                    }
                }
            }
        }
        
        if shouldShowSpinner {
            spinnerShown = true
            if self.responds(to: #selector(Spinnerable.showSpinner)) {
                self.perform(#selector(Spinnerable.showSpinner))
            }
        }
    }
}

public extension UIView {
    
    @discardableResult
    func onTouchAsync(showSpinner: Bool = true, _ action: @escaping (@escaping (Error?) -> Void) -> Void) -> Self {
        return self.onTouch { [weak self] in
            self?.parentViewController?.navigationController?.performAsyncAction(withSpinner: showSpinner, action)
        }
    }
}

public extension UIButton {
    
    @discardableResult
    func onClickAsync(showSpinner: Bool = true, _ action: @escaping (@escaping (Error?) -> Void) -> Void) -> Self {
        return self.onClick { [weak self] in
            self?.parentViewController?.navigationController?.performAsyncAction(withSpinner: showSpinner, action)
        }
    }
}

