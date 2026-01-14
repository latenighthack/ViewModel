//
//  UICollectionView+Flow.swift
//  app
//
//  Created by Mike Roberts on 2024-08-09.
//

import UIKit
import Core

class SectionTypedCellLayoutProvider: UIViewCellLayoutProvider {
    var reuseIdentifier: String
    var section: Int!
    var type: ItemMatcher
    
    init(section: Int, type: ItemMatcher, reuseIdentifier: String) {
        self.section = section
        self.type = type
        self.reuseIdentifier = reuseIdentifier
    }
    
    func matches(index: IndexPath, item: AnyObject) -> Bool {
        return index.section == self.section && self.type.matches(item: item)
    }
}

protocol ItemMatcher {
    func matches(item: AnyObject) -> Bool
}

protocol BindingItem : ItemMatcher {
    var cellType: UICollectionViewCell.Type { get }
    var reuseIdentifier: String { get }
}

struct WrappedCloseable: Closeable {
    let flowClosable: Viewmodel_libFlowCloseable
    
    func close() {
        self.flowClosable.close()
    }
}

@resultBuilder
struct SectionBuilder {
    static func buildBlock(_ sections: Section...) -> [Section] {
        return sections
    }
    
    static func buildBlock(_ items: BindingItem...) -> [BindingItem] {
        return items
    }
}

struct Section {
    let flow: any Kotlinx_coroutines_coreFlow
    let subitems: [BindingItem]
    
    init(flow: any Kotlinx_coroutines_coreFlow, @SectionBuilder items: () -> [BindingItem]) {
        self.flow = flow
        self.subitems = items()
    }
}

struct Layout<ViewModel> : BindingItem {
    var cellType: UICollectionViewCell.Type
    var reuseIdentifier: String
    
    init(cellType: UICollectionViewCell.Type, reuseIdentifier: String) {
        self.cellType = cellType
        self.reuseIdentifier = reuseIdentifier
    }

    func matches(item: AnyObject) -> Bool {
        return item is ViewModel
    }
}

private var CollectionViewDataSource = 2930

extension UICollectionView {
    @discardableResult
    func sections(inScope: Viewmodel_libBindingScope, @SectionBuilder _ content: () -> [Section]) -> Self {
        let sections = content()
        let flows = sections.map { section in
            section.flow
        }
        var providers = [UIViewCellLayoutProvider]()
        
        sections.enumerated().forEach { [weak self] (index, section) in
            guard let self = self else {
                return
            }
        
            for subitem in section.subitems {
                self.register(subitem.cellType, forCellWithReuseIdentifier: subitem.reuseIdentifier)
                
                providers.append(SectionTypedCellLayoutProvider(
                    section: index,
                    type: subitem,
                    reuseIdentifier: subitem.reuseIdentifier
                ))
            }
        }
        
        let dataSource = UICollectionViewFlowListDataSource(scope: inScope, sections: flows, providers: providers)
        
        self.dataSource = dataSource
        self.delegate = dataSource
        
        objc_setAssociatedObject(self, &CollectionViewDataSource, dataSource, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        
        dataSource.bind(collectionView: self, completionCallback: { [weak self] in
            guard self != nil else {
                return
            }
        }) { [weak self] _ in
            guard self != nil else {
                return
            }
        }
        
        return self
    }
}

struct WeakValue<Value> where Value: AnyObject {
    weak var value: Value?
}

class WeakValueCache<Key : Hashable, Value> where Value: AnyObject {
    private var values = [Key: WeakValue<Value>]()
    
    func setKey(_ key: Key, for value: Value) {
        self.values[key] = WeakValue(value: value)
    }
    
    func valueForKey(_ key: Key) -> Value? {
        guard let wrapper = self.values[key] else {
            return nil
        }
        
        return wrapper.value
    }
    
    func getOrSetKey(_ key: Key, valueLookup: (Key) -> Value) -> Value? {
        guard let value = valueForKey(key) else {
            let value = valueLookup(key)
            
            self.setKey(key, for: value)
            
            return value
        }
        
        return value
    }

    func clear() {
        self.values.removeAll()
    }
}

protocol UIViewCellLayoutProvider {
    func matches(index: IndexPath, item: AnyObject) -> Bool
    
    var reuseIdentifier: String { get }
}

class UICollectionViewFlowListDataSource: Viewmodel_libFlowListDataSource<Viewmodel_libViewModel, AnyObject>, UICollectionViewDataSource, UICollectionViewDelegate, UIScrollViewDelegate {
    
    private let providers: [UIViewCellLayoutProvider]
    private let cache = WeakValueCache<IndexPath, AnyObject>()
    
    init(
        scope: Viewmodel_libBindingScope,
        sections: [any Kotlinx_coroutines_coreFlow],
        providers: [UIViewCellLayoutProvider]
    ) {
        self.providers = providers
        
        super.init(scope: scope, items: sections)
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        return collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: kind, for: indexPath)
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let existingCell = collectionView.cellForItem(at: indexPath)
        if existingCell != nil {
            return existingCell!
        }

        let model = self.cache.getOrSetKey(indexPath) { indexPath in
            return self.currentItemInSection(section: Int32(indexPath.section), index: Int32(indexPath.item))! as AnyObject
        } as! Viewmodel_libViewModel

        let firstIndex = self.providers.firstIndex { provider -> Bool in
            provider.matches(index: indexPath, item: model as AnyObject)
        }
        
        guard let index = firstIndex else {
            fatalError("No matching layout")
        }
        
        let provider = self.providers[index]
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: provider.reuseIdentifier, for: indexPath)
        
//        self.preProcessCell(cell)

        let closeable = WrappedCloseable(flowClosable: self.attach(collectionViewCell: cell, model: model))
        
        let boundCell = cell as? any ViewModelBoundCell
        
        let sel = #selector(ViewModelBoundCell.viewModelDidChange(_ :))
        
        (boundCell as AnyObject).perform(sel, with: model)
//        boundCell?.perform(sel, with: model)
        
//        (boundCell?.viewModelDidChange ?? { _ in })(model)
//        (cell as? ViewModelBoundCell<any Viewmodel_libViewModel, any>)
//        (cell as? ViewModelBoundCell)?.viewModelDidChange(model)
        (cell as? CloseableCollectionViewCell)?.attach(closeable)

        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let cell = collectionView.cellForItem(at: indexPath)
        let model = self.cache.getOrSetKey(indexPath) { indexPath in
            return self.currentItemInSection(section: Int32(indexPath.section), index: Int32(indexPath.item)) as AnyObject
        }
        
//        (cell as? CellResponder)?.onSelected(model!)
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return Int(self.getSectionCount())
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return Int(self.getItemsInSection(index: Int32(section)))
    }
    
    override func cellStateDidChange(cell: UITableViewCell, model: Viewmodel_libViewModel, state: Any) {
    }
    
    override func collectionViewCellStateDidChange(
        cell: UICollectionViewCell,
        model: Viewmodel_libViewModel,
        state: Any
    ) {
        ((cell as? any ViewModelBoundCell)?.viewModelStateDidChange(_:) ?? { _ in })(state)
    }
}

@objc public protocol ViewModelBoundCell: AnyObject {
    @objc optional func viewModelDidChange(_ viewModel: Viewmodel_libViewModel)
    @objc optional func viewModelStateDidChange(_ state: Any)
}

public protocol Closeable {
    func close()
}

open class CloseableCollectionViewCell: UICollectionViewCell {
    private var closeable: Closeable?
    
    public func attach(_ closeable: Closeable) {
        self.closeable = closeable
    }
    
    open override func prepareForReuse() {
        super.prepareForReuse()
        
        self.closeable?.close()
        self.closeable = nil
    }
}
