//
//  ViewController.swift
//  app
//
//  Created by Mike Roberts on 2024-08-02.
//

import UIKit
import ViewModelSupport
import Core

class HomeCollectionViewCell: CloseableCollectionViewCell {
}

class HomeACollectionViewCell: HomeCollectionViewCell, ViewModelBoundCell {
    static let reusableCellIdentifier = "HomeCollectionViewCellA"
    
    private var titleLabel: UILabel!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.titleLabel = UILabel()
            .disableAutoresizingConstraints()
            .textColor(.blue)
            .addTo(self.contentView)
            .constrainLeading(toLeadingOf: self.contentView, offsetBy: 10.0)
            .constrainCenterY(to: self.contentView)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func viewModelDidChange(_ viewModel: any Viewmodel_libViewModel) {
        let viewModel = viewModel as! any IHomeListItemAViewModel
    }
    
    func viewModelStateDidChange(_ state: Any) {
        let state = state as! IHomeListItemAViewModelState
        
        self.titleLabel.text = state.titleA
    }
}

class HomeBCollectionViewCell: HomeCollectionViewCell, ViewModelBoundCell {
    static let reusableCellIdentifier = "HomeCollectionViewCellB"
    
    private var titleLabel: UILabel!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.titleLabel = UILabel()
            .disableAutoresizingConstraints()
            .textColor(.red)
            .addTo(self.contentView)
            .constrainLeading(toLeadingOf: self.contentView, offsetBy: 10.0)
            .constrainCenterY(to: self.contentView)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func viewModelDidChange(_ viewModel: any Viewmodel_libViewModel) {
        let viewModel = viewModel as! any IHomeListItemBViewModel
    }
    
    func viewModelStateDidChange(_ state: Any) {
        let state = state as! IHomeListItemBViewModelState
        
        self.titleLabel.text = state.titleB
    }
}

class HomeViewController: CoreNavigableViewController<IHomeViewModel, IHomeViewModelState, IHomeViewModelArgs> {
    
    private var itemView: UICollectionView!
    private var startButton: UIButton!

    override func createViewModel() -> IHomeViewModel {
        return HomeReporterProxy(original: HomeViewModel(
            args: self.args,
            basicStore: self.core.simpleStore,
            service: self.core.dummyService
        ), reporter: AmplitudeViewModelReporter())
    }
    
    override func setup() {
        let configuration = UICollectionLayoutListConfiguration(appearance: .plain)
        let layout = UICollectionViewCompositionalLayout.list(using: configuration)
        
        self.startButton = UIButton(frame: .zero)
            .disableAutoresizingConstraints()
            .setTitle("Start")
            .addTo(self.view)
            .constrainTop(toTopOf: self.view, useSafeArea: true)
        
        self.itemView = UICollectionView(frame: .zero, collectionViewLayout: layout)
            .disableAutoresizingConstraints()
            .addTo(self.view)
            .constrainTop(toBottomOf: self.startButton)
            .constrainLeading(toLeadingOf: self.view)
            .constrainTrailing(toTrailingOf: self.view)
            .constrainBottom(toBottomOf: self.view, useSafeArea: true)
    }
    
    override func onBindView(viewModel: any IHomeViewModel) {
        self.startButton.onClickAsync(viewModel.onStartTapped(completionHandler:))
        
        self.itemView.sections(inScope: self.bindingScope) {
            Section(flow: self.viewModel.items) {
                Layout<IHomeListItemAViewModel>(
                    cellType: HomeACollectionViewCell.self,
                    reuseIdentifier: HomeACollectionViewCell.reusableCellIdentifier
                )
                Layout<IHomeListItemBViewModel>(
                    cellType: HomeBCollectionViewCell.self,
                    reuseIdentifier: HomeBCollectionViewCell.reusableCellIdentifier
                )
            }
        }
    }
}
