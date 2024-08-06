//
//  ViewController.swift
//  app
//
//  Created by Mike Roberts on 2024-08-02.
//

import UIKit
import ViewModelSupport
import Core

class SwiftCollector<T>: Kotlinx_coroutines_coreFlowCollector {
    typealias Callback = (T) -> Void
    
    private var callback: Callback
    
    init(_ callback: @escaping Callback) {
        self.callback = callback
    }
    
    func emit(value: Any?) async throws {
        guard let typedValue = value as? T else {
            return
        }
        
        self.callback(typedValue)
    }
}

class CoreNavigableViewController<
    ViewModelType: Viewmodel_libNavigableViewModel,
    StateType,
    NavArgType: Viewmodel_libNavigatorArgs
>: BaseNavigableViewController<ViewModelType, StateType, NavArgType> {
    
    override final func attachViewModelTo(observer: @escaping (StateType) -> Void) -> VMCloseable {
        self.viewModel.state.collect(collector: SwiftCollector({ [observer] (value: StateType) in
            observer(value)
        }), completionHandler: { _ in
        })
        
        return {
//            closeable.close()
        }
    }
}

class HomeViewController: CoreNavigableViewController<HomeViewModel, IHomeViewModelState, IHomeViewModelArgs> {

    override func createViewModel() -> HomeViewModel {
        return HomeViewModel(args: self.args)
    }
}
