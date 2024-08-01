import UIKit
import ViewModelCore

protocol ArgViewController {
    associatedtype ArgType

    var args: ArgType { get }

    init(args: ArgType)
}

class BaseViewController<ViewModelType : ViewModel, StateType>: UIViewController {

    private var watchCloseable: FlowCloseable?
    lazy var viewModel: ViewModelType! = self.createViewModel()
    var bindingScope: BindingScope

    init() {
        self.bindingScope = BindingScope()

        super.init(nibName: nil, bundle: nil)
    }

    init(bindingScope: BindingScope) {
        self.bindingScope = bindingScope

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    deinit {
        self.bindingScope.cancel()
    }

    open func createViewModel() -> ViewModelType {
        preconditionFailure("This method must be implemented")
    }
    
    private func setupWatchable(for viewModel: ViewModelType) {
        if self.watchCloseable == nil {
            self.watchCloseable = FlowToolsKt.watchFlow(flow: viewModel.state) { [weak self] (anyState: Any?) in
                guard let state = anyState as? StateType else { return }
                guard let self = self else { return }

                if Thread.isMainThread {
                    self.onStateChanged(viewModel: self.viewModel, state: state)
                } else {
                    DispatchQueue.main.async {
                        self.onStateChanged(viewModel: self.viewModel, state: state)
                    }
                }
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        self.onBindView(viewModel: self.viewModel)

        self.setupWatchable(for: self.viewModel)

        // force intiial state change synchronously
        self.onStateChanged(
            viewModel: self.viewModel,
            state: self.viewModel.initialState as! StateType
        )
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        self.setupWatchable(for: self.viewModel)
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        if let watchCloseable = self.watchCloseable {
            self.watchCloseable = nil
            watchCloseable.close()
        }
    }

    open func onBindView(viewModel: ViewModelType) {
    }

    open func onStateChanged(viewModel: ViewModelType, state: StateType) {
    }
}

class BaseNavigableViewController<ViewModelType : NavigableViewModel, StateType, NavArgType>:
    BaseViewController<ViewModelType, StateType>, ArgViewController {

    typealias ArgType = NavArgType

    var args: ArgType

    required init(args: ArgType) {
        self.args = args

        super.init()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
