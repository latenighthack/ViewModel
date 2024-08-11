//
//  CoreNavigableViewController.swift
//  app
//
//  Created by Mike Roberts on 2024-08-09.
//

import UIKit
import Core
import ViewModelSupport

class CoreNavigableViewController<
    ViewModelType: Viewmodel_libNavigableViewModel,
    StateType,
    NavArgType: Viewmodel_libNavigatorArgs
>: BaseNavigableViewController<ViewModelType, StateType, NavArgType> {
    var bindingScope: Viewmodel_libBindingScope!
    
    required init(args: BaseNavigableViewController<ViewModelType, StateType, NavArgType>.ArgType) {
        super.init(args: args)
        
        self.bindingScope = Viewmodel_libBindingScope()
    }
    
    deinit {
        self.bindingScope.cancel()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override final func attachViewModelTo(observer: @escaping (StateType) -> Void) -> VMCloseable {
        self.bindingScope.collect(flow: self.viewModel.state) { [observer] anyValue in
            guard let value = anyValue as? StateType else {
                return
            }
            
            observer(value)
        }
        
        observer(self.viewModel.initialState as! StateType)
        
        return { [weak self] in
            self?.bindingScope.cancel()
        }
    }
}
