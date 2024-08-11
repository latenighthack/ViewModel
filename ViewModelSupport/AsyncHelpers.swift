//
//  AsyncHelpers.swift
//  
//
//  Created by Mike Roberts on 2024-08-03.
//

import Foundation

public func runOnMain(_ closure: @escaping () -> Void) {
    if Thread.isMainThread {
        closure()
    } else {
        DispatchQueue.main.async {
            closure()
        }
    }
}

public func runOnMainSync(_ closure: @escaping () -> Void) {
    if Thread.isMainThread {
        closure()
    } else {
        DispatchQueue.main.sync {
            closure()
        }
    }
}

public func runOnMainLater(_ closure: @escaping () -> Void) {
    DispatchQueue.main.async {
        closure()
    }
}

public func runOnMainAfter(delay: TimeInterval, _ closure: @escaping () -> Void) {
    DispatchQueue.main.asyncAfter(deadline: .now() + delay, execute: DispatchWorkItem {
        closure()
    })
}

public func runOnMainRepeated(delay: TimeInterval = 0.0, repeatTime: TimeInterval, _ closure: @escaping () -> Void) -> (() -> Void) {
    var cancelled = false
    
    DispatchQueue.global().async {
        if delay > 0 {
            usleep(useconds_t(delay * 1000000))
        }
        
        while !cancelled {
            usleep(useconds_t(repeatTime * 1000000))
            
            if cancelled {
                break
            }
            
            DispatchQueue.main.async {
                if !cancelled {
                    closure()
                }
            }
        }
    }
    
    return {
        cancelled = true
    }
}
