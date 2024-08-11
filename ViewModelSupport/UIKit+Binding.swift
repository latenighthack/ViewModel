//
//  UIKit+Binding.swift
//
//
//  Created by Mike Roberts on 2024-08-09.
//

import UIKit

public extension UIViewController {
    
    func showSpinner() {
    }
    
    func hideSpinner() {
    }
    
    func showErrorDialog(error: Error) {
    }
    
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
                    self.hideSpinner()
                }
                
                if let err = error {
                    self.showErrorDialog(error: err)
                }
            }
        }
        
        if shouldShowSpinner {
            spinnerShown = true
            self.showSpinner()
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

