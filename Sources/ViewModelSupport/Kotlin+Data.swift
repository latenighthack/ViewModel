//
//  File.swift
//  
//
//  Created by Mike Roberts on 2024-08-01.
//

import Foundation
import ViewModelCore

extension Data {
    func toByteArray() -> KotlinByteArray {
        let kotlinByteArray: KotlinByteArray = KotlinByteArray.init(size: Int32(self.count))
        
        for (index, datum) in self.enumerated() {
            kotlinByteArray.set(index: Int32(index), value: Int8(bitPattern: UInt8(datum)))
        }
        
        return kotlinByteArray
    }
}

extension KotlinByteArray {
    func toData() -> Data {
        return IosToolsKt.byteArrayToData(bytes: self)
    }
}
