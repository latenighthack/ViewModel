import UIKit

public protocol ArgViewController {
    associatedtype ArgType

    var args: ArgType { get }

    init(args: ArgType)
}

public typealias VMCloseable = () -> Void

open class BaseViewController<ViewModelType, StateType>: UIViewController {

    private var watchCloseable: VMCloseable?
    public lazy var viewModel: ViewModelType! = self.createViewModel()
//    var bindingScope: BindingScope
//
    init() {
//        self.bindingScope = BindingScope()

        super.init(nibName: nil, bundle: nil)
    }
//
//    init(bindingScope: BindingScope) {
//        self.bindingScope = bindingScope
//
//        super.init(nibName: nil, bundle: nil)
//    }

    required public init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

//    deinit {
//        self.bindingScope.cancel()
//    }

    open func createViewModel() -> ViewModelType {
        preconditionFailure("This method must be implemented")
    }
    
    private func setupWatchable(for viewModel: ViewModelType) {
        if self.watchCloseable == nil {
            self.watchCloseable = self.attachViewModelTo(observer: { [weak self] state in
                guard let self = self else { return }

                if Thread.isMainThread {
                    self.onStateChanged(viewModel: self.viewModel, state: state)
                } else {
                    DispatchQueue.main.async {
                        self.onStateChanged(viewModel: self.viewModel, state: state)
                    }
                }
            })
        }
    }
    
    open func attachViewModelTo(observer: @escaping (StateType) -> Void) -> VMCloseable {
        fatalError("unimplemented")
    }

    open override func viewDidLoad() {
        super.viewDidLoad()

        self.onBindView(viewModel: self.viewModel)

        self.setupWatchable(for: self.viewModel)
    }

    open override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        self.setupWatchable(for: self.viewModel)
    }

    open override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        if let watchCloseable = self.watchCloseable {
            self.watchCloseable = nil
            
            watchCloseable()
        }
    }

    open func onBindView(viewModel: ViewModelType) {
    }

    open func onStateChanged(viewModel: ViewModelType, state: StateType) {
    }
}

open class BaseNavigableViewController<ViewModelType, StateType, NavArgType>:
    BaseViewController<ViewModelType, StateType>, ArgViewController {

    public typealias ArgType = NavArgType

    public var args: ArgType

    public required init(args: ArgType) {
        self.args = args

        super.init()
    }

    public required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
