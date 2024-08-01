//
//  File.swift
//  
//
//  Created by Mike Roberts on 2024-08-01.
//

import Foundation
import ViewModelCore

extension Kotlinx_coroutines_coreFlow {
    func watch<T>(block: @escaping (T) -> Void) -> FlowCloseable {
        FlowToolsKt.watchFlow(flow: self) { (value) in
            block(value as! T)
        }
    }
}
