//
//  UIKit+Extensions.swift
//
//  Created by Mike Roberts on 2024-08-09.
//

import Foundation
import UIKit

private var UIButtonClickKey = 1
private var UITextFieldChangeKey = 2
private var GestureDelegateKey = 3
private var UILongPressKey = 4

public protocol ThemedColors {
    var isDark: Bool { get }
    var statusBarDefaultTheme: UIStatusBarStyle { get }
    var shadowColor: UIColor { get }
    var primaryTextColor: UIColor { get }
    var blackIfLightMode: UIColor { get }
    var whiteIfLightMode: UIColor { get }
    var secondaryTextColor: UIColor { get }
    var tertiaryTextColor: UIColor { get }
    var statusBarColor: UIColor { get }
    var navigationBarColor: UIColor { get }
    var backgroundColor: UIColor { get }
    var backgroundHighlightColor: UIColor { get }
    var tabBackgroundColor: UIColor { get }
    var tabIconColor: UIColor { get }
    var tabIconSelectedColor: UIColor { get }
    var contrastAccentColor: UIColor { get }
    var lightAccentColor: UIColor { get }
    var shadowAccentColor: UIColor { get }
    var accentButtonImage: UIImage { get }
}

fileprivate extension UIImage {
    static func button(named: String) -> UIImage {
        return UIImage(named: named)!
            .resizableImage(withCapInsets: .init(top: 20.0, left: 20.0, bottom: 20.0, right: 20.0), resizingMode: .stretch)
    }
}

public struct LightThemedColors: ThemedColors {
    public var isDark: Bool = false
    public var statusBarDefaultTheme: UIStatusBarStyle = .darkContent
    public var blackIfLightMode: UIColor = .fromRgb(0x000000)
    public var whiteIfLightMode: UIColor = .fromRgb(0xffffff)
    public var shadowColor: UIColor = .fromRgb(0x58585B)
    public var primaryTextColor: UIColor = .fromRgb(0x58585B)
    public var secondaryTextColor: UIColor = .fromRgb(0x9B9CA2)
    public var tertiaryTextColor: UIColor = .fromRgb(0xBAB9BF)
    public var statusBarColor: UIColor = .fromRgb(0xd4d4d4)
    public var navigationBarColor: UIColor = .fromRgb(0x303030)
    public var backgroundColor: UIColor = .fromRgb(0xE9EDEF)
    public var backgroundHighlightColor: UIColor = .fromRgb(0xDFE2E5)
    public var tabBackgroundColor: UIColor = .fromRgb(0xDFE2E5)
    public var tabIconColor: UIColor = .fromRgb(0x55565A)
    public var tabIconSelectedColor: UIColor = .fromRgb(0x6B00EA)
    public var contrastAccentColor: UIColor = .fromRgb(0x8b64ff)
    public var lightAccentColor: UIColor = .fromRgb(0x8E5FFF)
    public var shadowAccentColor: UIColor = .fromRgb(0xA17BFF)
    public var accentButtonImage: UIImage = UIImage.button(named: "btn-light-accent")
}

public struct DarkThemedColors: ThemedColors {
    public var isDark: Bool = true
    public var statusBarDefaultTheme: UIStatusBarStyle = .lightContent
    public var blackIfLightMode: UIColor = .fromRgb(0xffffff)
    public var whiteIfLightMode: UIColor = .fromRgb(0x000000)
    public var shadowColor: UIColor = .fromRgb(0x000000)
    public var primaryTextColor: UIColor = .fromRgb(0xBBB9BF)
    public var secondaryTextColor: UIColor = .fromRgb(0x88878E)
    public var tertiaryTextColor: UIColor = .fromRgb(0x58585B)
    public var statusBarColor: UIColor = .fromRgb(0x191b1d)
    public var navigationBarColor: UIColor = .fromRgb(0x303030)
    public var backgroundColor: UIColor = .fromRgb(0x111213)
    public var backgroundHighlightColor: UIColor = .fromRgb(0x222123)
    public var tabBackgroundColor: UIColor = .fromRgb(0x222123)
    public var tabIconColor: UIColor = .fromRgb(0x828D90)
    public var tabIconSelectedColor: UIColor = .fromRgb(0x7A44FF)
    public var contrastAccentColor: UIColor = .fromRgb(0x7A44FF)
    public var lightAccentColor: UIColor = .fromRgb(0x550BBB)
    public var shadowAccentColor: UIColor = .fromRgb(0x1b0947)
    public var accentButtonImage: UIImage = UIImage.button(named: "btn-dark-accent")
}

var themedColors: ThemedColors = LightThemedColors()

public func updateTheme(isDark: Bool) {
    if isDark {
        themedColors = DarkThemedColors()
    } else {
        themedColors = LightThemedColors()
    }
}

extension UIEdgeInsets {
    public static func equalTo(_ value: CGFloat) -> UIEdgeInsets {
        return UIEdgeInsets(top: value, left: value, bottom: value, right: value)
    }
}

class UISwitchActionCallback {
    
    private let callback: (Bool) -> Void
    
    init(_ callback: @escaping (Bool) -> Void) {
        self.callback = callback
    }
    
    @objc func invoke(sender: UISwitch) {
        self.callback(sender.isOn)
    }
}

class UIButtonActionCallback {
    
    private let callback: () -> Void
    
    init(_ callback: @escaping () -> Void) {
        self.callback = callback
    }
    
    @objc func invoke() {
        self.callback()
    }
}

extension UIView {
    public var isDark: Bool {
        get {
            return themedColors.isDark
        }
    }
}

class UITextFieldChangeCallback {
    
    private let callback: (String) -> Void
    
    init(_ callback: @escaping (String) -> Void) {
        self.callback = callback
    }
    
    @objc func invoke(_ textField: UITextField) {
        self.callback(textField.text ?? "")
    }
}

public extension UITableView {
    @discardableResult
    func removeTopPadding() -> Self {
        if #available(iOS 15.0, *) {
            self.sectionHeaderTopPadding = 0.0
        }
        
        return self
    }
    
    @discardableResult
    func setupDefaultStyle() -> Self {
        self.rowHeight = UITableView.automaticDimension
        self.estimatedRowHeight = 108.0
        self.insetsContentViewsToSafeArea = false
        self.insetsLayoutMarginsFromSafeArea = false
        self.backgroundColor = .clear
        self.separatorStyle = .none
        
        return self
    }
}

public extension UIButton {
    
    @discardableResult
    func setTitle(_ string: String) -> Self {
        self.setTitle(string, for: .normal)
        return self
    }
    
    @discardableResult
    func onClick(_ action: @escaping () -> Void) -> Self {
        let target = UIButtonActionCallback(action)
        
        self.addTarget(target, action: #selector(UIButtonActionCallback.invoke), for: .touchUpInside)
        
        objc_setAssociatedObject(self, &UIButtonClickKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        
        return self
    }
    
    @discardableResult
    func onLongPress(_ action: @escaping () -> Void) -> Self {
        let target = UIButtonActionCallback(action)
        let tapAction = UILongPressGestureRecognizer(target: target, action: #selector(UIButtonActionCallback.invoke))
                                               
        tapAction.minimumPressDuration = 0.3
        self.isUserInteractionEnabled = true
        self.addGestureRecognizer(tapAction)
        
        objc_setAssociatedObject(self, &UILongPressKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        
        return self
    }
    
    @discardableResult
    func fontSize(_ size: CGFloat) -> Self {
        self.titleLabel?.font = self.titleLabel?.font.withSize(size)
        
        return self
    }
    
    @discardableResult
    func setFont(_ font: UIFont) -> Self {
        self.titleLabel?.font = font
        
        return self
    }
}

public extension UILabel {
    @discardableResult
    func setText(_ string: String) -> Self {
        self.text = string
        return self
    }
    
    @discardableResult
    func textAlign(_ alignment: NSTextAlignment) -> Self {
        self.textAlignment = alignment
        return self
    }
    
    @discardableResult
    func textColor(_ color: UIColor) -> Self {
        self.textColor = color
        return self
    }
    
    @discardableResult
    func fontSize(_ size: CGFloat) -> Self {
        self.font = self.font.withSize(size)
        return self
    }
    
    @discardableResult
    func withFont(_ font: UIFont) -> Self {
        self.font = font
        return self
    }
    
    @discardableResult
    func setSingleLine() -> Self {
        self.lineBreakMode = .byTruncatingTail
        self.numberOfLines = 1
        self.adjustsFontSizeToFitWidth = false
        return self
    }
    
    @discardableResult
    func setMaxLines(_ count: Int) -> Self {
        self.lineBreakMode = .byWordWrapping
        self.numberOfLines = count
        self.adjustsFontSizeToFitWidth = false
        return self
    }
}

public extension UIFont {
    class func rounded(ofSize size: CGFloat, weight: UIFont.Weight) -> UIFont {
        let systemFont = UIFont.systemFont(ofSize: size, weight: weight)
        let font: UIFont
        
        if let descriptor = systemFont.fontDescriptor.withDesign(.rounded) {
            font = UIFont(descriptor: descriptor, size: size)
        } else {
            font = systemFont
        }
        return font
    }
    
    class var rollBold: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize, weight: .bold)
        }
    }
    
    class var rollExtraBoldTitle: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.7, weight: .heavy)
        }
    }
    
    class var rollPromoTitle: UIFont {
        get {
            return systemFont(ofSize: UIFont.systemFontSize * 5, weight: .black)
        }
    }
    
    class var rollExtraBlack: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 2, weight: .black)
        }
    }
    
    class var rollBoldTitle: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.6, weight: .semibold)
        }
    }
    
    class var rollBoldHeading: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.4, weight: .semibold)
        }
    }
    
    class var rollExtraBoldHeading: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.4, weight: .bold)
        }
    }
    
    class var rollSmall: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.15, weight: .regular)
        }
    }
    
    class var rollMedium: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 0.95, weight: .regular)
        }
    }
    
    class var rollMediumSmall: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.125, weight: .medium)
        }
    }
    
    class var rollSubtext: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 0.9, weight: .regular)
        }
    }
    
    class var rollTiny: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 0.8, weight: .regular)
        }
    }
    
    class var rollExtraTiny: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 0.7, weight: .medium)
        }
    }
    
    class var rollRegular: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.2, weight: .medium)
        }
    }
    
    class var rollDraftText: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.285, weight: .semibold)
        }
    }
    
    class var rollMessage: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.325, weight: .regular)
        }
    }
    
    class var rollMessageMedium: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.25, weight: .medium)
        }
    }
    
    class var rollHeading: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.3, weight: .medium)
        }
    }
    
    class var rollRegularButton: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.3, weight: .medium)
        }
    }
    
    class var rollBoldButton: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.55, weight: .black)
        }
    }
    
    class var rollSemiboldButton: UIFont {
        get {
            return rounded(ofSize: UIFont.systemFontSize * 1.3, weight: .semibold)
        }
    }
    
    class var rollTitle: UIFont {
        get {
            
            return rounded(ofSize: UIFont.systemFontSize * 1.6, weight: .bold)
        }
    }
}

public extension UILabel {
    @discardableResult
    func onClick(_ action: @escaping () -> Void) -> Self {
        let target = UIButtonActionCallback(action)
        let tapAction = UITapGestureRecognizer(target: target, action: #selector(UIButtonActionCallback.invoke))
                                               
        self.isUserInteractionEnabled = true
        self.addGestureRecognizer(tapAction)
        
        objc_setAssociatedObject(self, &UIButtonClickKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        
        return self
    }
}

public extension UISwitch {
    @discardableResult
    func onToggle(_ action: @escaping (Bool) -> Void) -> Self {
        let target = UISwitchActionCallback(action)
        
        self.addTarget(target, action: #selector(UISwitchActionCallback.invoke), for: .valueChanged)
        
        objc_setAssociatedObject(self, &UIButtonClickKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        
        return self
    }
}

@objc class IgnoreOthersGestureDelegate : NSObject, UIGestureRecognizerDelegate {
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        return touch.view == gestureRecognizer.view
    }
}

public enum UIViewBorderPosition {
    case top
    case bottom
    case left
    case right
}

private class TouchGestureRecognizer: UIGestureRecognizer {
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent) {
        self.state = .recognized
    }
}

public extension UIView {
    @discardableResult
    func onTouch(_ action: @escaping () -> Void) -> Self {
        let target = UIButtonActionCallback(action)
        let tapAction = UITapGestureRecognizer(target: target, action: #selector(UIButtonActionCallback.invoke))
        let gestureDelegate = IgnoreOthersGestureDelegate()
        
        self.isUserInteractionEnabled = true
        self.addGestureRecognizer(tapAction)
        
        tapAction.delegate = gestureDelegate
        
        objc_setAssociatedObject(self, &UIButtonClickKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        objc_setAssociatedObject(self, &GestureDelegateKey, gestureDelegate, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        
        return self
    }
    
    @discardableResult
    func onBeganTouch(_ action: @escaping () -> Void) -> Self {
        let target = UIButtonActionCallback(action)
        let tapAction = TouchGestureRecognizer(target: target, action: #selector(UIButtonActionCallback.invoke))
        let gestureDelegate = IgnoreOthersGestureDelegate()
        
        self.isUserInteractionEnabled = true
        self.addGestureRecognizer(tapAction)
        
        tapAction.delegate = gestureDelegate
        
        objc_setAssociatedObject(self, &UIButtonClickKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        objc_setAssociatedObject(self, &GestureDelegateKey, gestureDelegate, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        
        return self
    }
    
    @discardableResult
    func addBorder(to: UIViewBorderPosition, width: CGFloat, color: UIColor) -> Self {
        let borderLayer = UIView()
        
        borderLayer.translatesAutoresizingMaskIntoConstraints = false
        borderLayer.backgroundColor = color
        
        self.addSubview(borderLayer)
        
        if to == .top || to == .bottom {
            NSLayoutConstraint.activate([
                borderLayer.heightAnchor.constraint(equalToConstant: width),
                borderLayer.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                borderLayer.trailingAnchor.constraint(equalTo: self.trailingAnchor)
            ])
        } else {
            NSLayoutConstraint.activate([
                borderLayer.widthAnchor.constraint(equalToConstant: width),
                borderLayer.topAnchor.constraint(equalTo: self.topAnchor),
                borderLayer.bottomAnchor.constraint(equalTo: self.bottomAnchor)
            ])
        }
        
        if to == .top {
            borderLayer.topAnchor.constraint(equalTo: self.topAnchor).isActive = true
        }
        else if to == .bottom {
            borderLayer.bottomAnchor.constraint(equalTo: self.bottomAnchor).isActive = true
        }
        else if to == .left {
            borderLayer.leadingAnchor.constraint(equalTo: self.leadingAnchor).isActive = true
        }
        else if to == .right {
            borderLayer.trailingAnchor.constraint(equalTo: self.trailingAnchor).isActive = true
        }
        
        return self
    }
    
    var firstResponder: UIView? {
        guard !isFirstResponder else { return self }

        for subview in subviews {
            if let firstResponder = subview.firstResponder {
                return firstResponder
            }
        }

        return nil
    }
    
    var parentViewController: UIViewController? {
        var parentResponder: UIResponder? = self.next
        while parentResponder != nil {
            if let viewController = parentResponder as? UIViewController {
                return viewController
            }
            parentResponder = parentResponder?.next
        }
        return nil
    }
    
    @discardableResult
    func addTo(_ view: UIView) -> Self {
        view.addSubview(self)
        
        return self
    }
    
    @discardableResult
    func addTo(_ view: UIView, below: UIView) -> Self {
        view.insertSubview(self, belowSubview: below)
        
        return self
    }
    
    @discardableResult
    func addTo(_ view: UIView, above: UIView) -> Self {
        view.insertSubview(self, aboveSubview: above)
        
        return self
    }
    
    @discardableResult
    func addTo(arranged: UIStackView) -> Self {
        arranged.addArrangedSubview(self)
        
        return self
    }
    
    @discardableResult
    func disableAutoresizingConstraints() -> Self {
        self.translatesAutoresizingMaskIntoConstraints = false
        
        return self
    }
    
    @discardableResult
    func constrainTop(toTopOf view: UIView, offsetBy offset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let topAnchor = useSafeArea ? view.safeAreaLayoutGuide.topAnchor : view.topAnchor
        NSLayoutConstraint.activate([
            self.topAnchor.constraint(equalTo: topAnchor, constant: offset)
        ])
        return self
    }

    @discardableResult
    func constrainTop(toBottomOf view: UIView, offsetBy offset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let bottomAnchor = useSafeArea ? view.safeAreaLayoutGuide.bottomAnchor : view.bottomAnchor
        NSLayoutConstraint.activate([
            self.topAnchor.constraint(equalTo: bottomAnchor, constant: offset)
        ])
        return self
    }

    @discardableResult
    func constrainBottom(toBottomOf view: UIView, offsetBy offset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let bottomAnchor = useSafeArea ? view.safeAreaLayoutGuide.bottomAnchor : view.bottomAnchor
        NSLayoutConstraint.activate([
            self.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -offset)
        ])
        return self
    }

    @discardableResult
    func constrainBottom(toTopOf view: UIView, offsetBy offset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let topAnchor = useSafeArea ? view.safeAreaLayoutGuide.topAnchor : view.topAnchor
        NSLayoutConstraint.activate([
            self.bottomAnchor.constraint(equalTo: topAnchor, constant: -offset)
        ])
        return self
    }

    @discardableResult
    func constrainLeading(toLeadingOf view: UIView, offsetBy offset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let leadingAnchor = useSafeArea ? view.safeAreaLayoutGuide.leadingAnchor : view.leadingAnchor
        NSLayoutConstraint.activate([
            self.leadingAnchor.constraint(equalTo: leadingAnchor, constant: offset)
        ])
        return self
    }

    @discardableResult
    func constrainTrailing(toTrailingOf view: UIView, offsetBy offset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let trailingAnchor = useSafeArea ? view.safeAreaLayoutGuide.trailingAnchor : view.trailingAnchor
        NSLayoutConstraint.activate([
            self.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -offset)
        ])
        return self
    }

    @discardableResult
    func constrainLeading(toTrailingOf view: UIView, offsetBy offset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let trailingAnchor = useSafeArea ? view.safeAreaLayoutGuide.trailingAnchor : view.trailingAnchor
        NSLayoutConstraint.activate([
            self.leadingAnchor.constraint(equalTo: trailingAnchor, constant: offset)
        ])
        return self
    }

    @discardableResult
    func constrainTrailing(toLeadingOf view: UIView, offsetBy offset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let leadingAnchor = useSafeArea ? view.safeAreaLayoutGuide.leadingAnchor : view.leadingAnchor
        NSLayoutConstraint.activate([
            self.trailingAnchor.constraint(equalTo: leadingAnchor, constant: -offset)
        ])
        return self
    }

    @discardableResult
    func constrainEdges(to view: UIView, insetBy insets: UIEdgeInsets = .zero, useSafeArea: Bool = false) -> Self {
        let topAnchor = useSafeArea ? view.safeAreaLayoutGuide.topAnchor : view.topAnchor
        let bottomAnchor = useSafeArea ? view.safeAreaLayoutGuide.bottomAnchor : view.bottomAnchor
        let leadingAnchor = useSafeArea ? view.safeAreaLayoutGuide.leadingAnchor : view.leadingAnchor
        let trailingAnchor = useSafeArea ? view.safeAreaLayoutGuide.trailingAnchor : view.trailingAnchor

        NSLayoutConstraint.activate([
            self.topAnchor.constraint(equalTo: topAnchor, constant: insets.top),
            self.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -insets.bottom),
            self.leadingAnchor.constraint(equalTo: leadingAnchor, constant: insets.left),
            self.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -insets.right)
        ])
        return self
    }

    @discardableResult
    func constrainCenterX(to view: UIView, offsetBy offset: CGFloat = 0, useSafeArea: Bool = false) -> Self {
        let centerXAnchor = useSafeArea ? view.safeAreaLayoutGuide.centerXAnchor : view.centerXAnchor
        NSLayoutConstraint.activate([
            self.centerXAnchor.constraint(equalTo: centerXAnchor, constant: offset)
        ])
        return self
    }

    @discardableResult
    func constrainCenterY(to view: UIView, offsetBy offset: CGFloat = 0, useSafeArea: Bool = false) -> Self {
        let centerYAnchor = useSafeArea ? view.safeAreaLayoutGuide.centerYAnchor : view.centerYAnchor
        NSLayoutConstraint.activate([
            self.centerYAnchor.constraint(equalTo: centerYAnchor, constant: offset)
        ])
        return self
    }

    @discardableResult
    func constrainWidth(toConstant width: CGFloat) -> Self {
        NSLayoutConstraint.activate([
            self.widthAnchor.constraint(equalToConstant: width)
        ])
        return self
    }

    @discardableResult
    func constrainHeight(toConstant height: CGFloat) -> Self {
        NSLayoutConstraint.activate([
            self.heightAnchor.constraint(equalToConstant: height)
        ])
        return self
    }

    @discardableResult
    func constrainSize(to size: CGSize) -> Self {
        NSLayoutConstraint.activate([
            self.widthAnchor.constraint(equalToConstant: size.width),
            self.heightAnchor.constraint(equalToConstant: size.height)
        ])
        return self
    }

    @discardableResult
    func constrainAspectRatio(_ ratio: CGFloat) -> Self {
        NSLayoutConstraint.activate([
            self.widthAnchor.constraint(equalTo: self.heightAnchor, multiplier: ratio)
        ])
        return self
    }

    @discardableResult
    func centerIn(_ view: UIView, useSafeArea: Bool = false) -> Self {
        let centerXAnchor = useSafeArea ? view.safeAreaLayoutGuide.centerXAnchor : view.centerXAnchor
        let centerYAnchor = useSafeArea ? view.safeAreaLayoutGuide.centerYAnchor : view.centerYAnchor

        NSLayoutConstraint.activate([
            self.centerYAnchor.constraint(equalTo: centerYAnchor),
            self.centerXAnchor.constraint(equalTo: centerXAnchor)
        ])

        return self
    }

    @discardableResult
    func constrainToEdges(of view: UIView, insetBy: UIEdgeInsets, useSafeArea: Bool = false) -> Self {
        let topAnchor = useSafeArea ? view.safeAreaLayoutGuide.topAnchor : view.topAnchor
        let bottomAnchor = useSafeArea ? view.safeAreaLayoutGuide.bottomAnchor : view.bottomAnchor
        let leadingAnchor = useSafeArea ? view.safeAreaLayoutGuide.leadingAnchor : view.leadingAnchor
        let trailingAnchor = useSafeArea ? view.safeAreaLayoutGuide.trailingAnchor : view.trailingAnchor

        NSLayoutConstraint.activate([
            self.topAnchor.constraint(equalTo: topAnchor, constant: insetBy.top),
            self.bottomAnchor.constraint(equalTo: bottomAnchor, constant: insetBy.bottom),
            self.leadingAnchor.constraint(equalTo: leadingAnchor, constant: insetBy.left),
            self.trailingAnchor.constraint(equalTo: trailingAnchor, constant: insetBy.right)
        ])

        return self
    }
    @discardableResult
    func setBackgroundColor(_ color: UIColor) -> Self {
        self.backgroundColor = color
        return self
    }
    
    @discardableResult
    func setCornerRadius(_ radius: CGFloat) -> Self {
        self.layer.cornerRadius = radius
        return self
    }
    
    @discardableResult
    func removeAllConstraints() -> Self {
        self.removeConstraints(self.constraints)
        return self
    }
    
    @discardableResult
    func addShadow(
        color: UIColor = .init(white: 0.0, alpha: 0.15),
        opacity: Float = 1.0,
        radius: CGFloat = 4.0,
        offset: CGSize = CGSize(width: 0.0, height: 2.0)
    ) -> Self {
        self.layer.shadowColor = color.cgColor
        self.layer.shadowOffset = offset
        self.layer.shadowOpacity = opacity
        self.layer.shadowRadius = radius
        self.layer.masksToBounds = false
        
        return self
    }
    
    @discardableResult
    func removeShadow() -> Self {
        self.layer.shadowOffset = .zero
        self.layer.shadowOpacity = 0.0
        self.layer.shadowRadius = 0.0
        self.layer.masksToBounds = true
        
        return self
    }
    
    @discardableResult
    func addTextShadow(
    ) -> Self {
        self.layer.shadowColor = UIColor(white: 0.0, alpha: 0.7).cgColor
        self.layer.shadowOffset = CGSize(width: 0.0, height: 0.5)
        self.layer.shadowOpacity = 0.7
        self.layer.shadowRadius = 1.0
        self.layer.masksToBounds = false
        self.layer.shouldRasterize = true
        
        return self
    }
}

public extension UIStatusBarStyle {
    static var appDefaultStyle: UIStatusBarStyle {
        get {
            return themedColors.statusBarDefaultTheme
        }
    }
}


public extension UIImage {
    
    class var accentButtonImage: UIImage {
        get {
            return themedColors.accentButtonImage
        }
    }
}

public extension UIColor {
    
    var hue: CGFloat {
        get {
            var hue: CGFloat = 0.0;
            var saturation: CGFloat = 0.0;
            var brightness: CGFloat = 0.0;
            var alpha: CGFloat = 0.0;
            
            self.getHue(&hue, saturation: &saturation, brightness: &brightness, alpha: &alpha)
            
            return hue
        }
    }
    
    var saturation: CGFloat {
        get {
            var hue: CGFloat = 0.0;
            var saturation: CGFloat = 0.0;
            var brightness: CGFloat = 0.0;
            var alpha: CGFloat = 0.0;
            
            self.getHue(&hue, saturation: &saturation, brightness: &brightness, alpha: &alpha)
            
            return saturation
        }
    }
    
    var brightness: CGFloat {
        get {
            var hue: CGFloat = 0.0
            var saturation: CGFloat = 0.0
            var brightness: CGFloat = 0.0
            var alpha: CGFloat = 0.0
            
            self.getHue(&hue, saturation: &saturation, brightness: &brightness, alpha: &alpha)
            
            return saturation
        }
    }
    
    func toIntArgb() -> Int32 {
        var red: CGFloat = 0.0
        var green: CGFloat = 0.0
        var blue: CGFloat = 0.0
        var alpha: CGFloat = 0.0
        
        self.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        return (Int32(alpha * 255) << 24) | (Int32(red * 255) << 16) | (Int32(green * 255) << 8) | (Int32(blue * 255) << 0) 
    }
    
    func lighten(by: CGFloat) -> UIColor {
        var hue: CGFloat = 0.0
        var saturation: CGFloat = 0.0
        var brightness: CGFloat = 0.0
        var alpha: CGFloat = 0.0
        
        self.getHue(&hue, saturation: &saturation, brightness: &brightness, alpha: &alpha)
        
        brightness += by
        brightness = max(brightness, 0.0)
        brightness = min(brightness, 1.0)
        
        return UIColor(hue: hue, saturation: saturation, brightness: brightness, alpha: alpha)
    }
    
    func desaturate(by: CGFloat, darken: CGFloat = 0.0) -> UIColor {
        var hue: CGFloat = 0.0
        var saturation: CGFloat = 0.0
        var brightness: CGFloat = 0.0
        var alpha: CGFloat = 0.0
        
        self.getHue(&hue, saturation: &saturation, brightness: &brightness, alpha: &alpha)
        
        saturation -= by
        saturation = max(saturation, 0.0)
        saturation = min(saturation, 1.0)
        
        brightness -= darken
        brightness = max(brightness, 0.0)
        brightness = min(brightness, 1.0)
        
        return UIColor(hue: hue, saturation: saturation, brightness: brightness, alpha: alpha)
    }
    
    class func fromRgb(_ color: Int) -> UIColor {
        return UIColor(
            red: CGFloat(Double(((color & 0xff0000) >> 16)) / 255.0),
            green: CGFloat(Double(((color & 0xff00) >> 8)) / 255.0),
            blue: CGFloat(Double(((color & 0xff) >> 0)) / 255.0),
            alpha: 1.0
        )
    }
    
    class func fromArgb(_ color: Int) -> UIColor {
        return UIColor(
            red: CGFloat(Double(((color & 0xff0000) >> 16)) / 255.0),
            green: CGFloat(Double(((color & 0xff00) >> 8)) / 255.0),
            blue: CGFloat(Double(((color & 0xff) >> 0)) / 255.0),
            alpha: CGFloat(Double(((color & 0xff000000) >> 24)) / 255.0)
        )
    }
    
    
    class var okColor: UIColor {
        get {
            return UIColor.fromRgb(0x00E94B)
        }
    }
    
    class var warningColor: UIColor {
        get {
            return UIColor.fromRgb(0xFFAE07)
        }
    }
    
    class var errorColor: UIColor {
        get {
            return UIColor.fromRgb(0xFF2A2A)
        }
    }
    
    class var accentColor: UIColor {
        get {
            return UIColor.fromRgb(0x7000FF)
        }
    }
    
    class var disabledAccentColor: UIColor {
        get {
            return UIColor.fromRgb(0x6451A5)
        }
    }
    
    class var darkAccentColor: UIColor {
        get {
            return UIColor.fromRgb(0x5304B8)
        }
    }
    
    class var googleBlue: UIColor {
        get {
            return UIColor.fromRgb(0x4285F4)
        }
    }
    
    class var overlayButtonBackgroundColor: UIColor {
        get {
            UIColor.fromRgb(0x141618).withAlphaComponent(0.72)
        }
    }
    
    private class var theme: ThemedColors {
        get {
            return themedColors
        }
    }
    
    // light mode
    /* */
    class var primaryTextColor: UIColor {
        get {
            return theme.primaryTextColor
        }
    }
    
    class var shadowColor: UIColor {
        get {
            return theme.shadowColor
        }
    }
    
    class var blackIfLightMode: UIColor {
        get {
            return theme.blackIfLightMode
        }
    }
    
    class var whiteIfLightMode: UIColor {
        get {
            return theme.whiteIfLightMode
        }
    }
    
    class var secondaryTextColor: UIColor {
        get {
            return theme.secondaryTextColor
        }
    }
    
    class var tertiaryTextColor: UIColor {
        get {
            return theme.tertiaryTextColor
        }
    }
    
    class var statusBarColor: UIColor {
        get {
            return theme.statusBarColor
        }
    }
    
    class var navigationBarColor: UIColor {
        get {
            return theme.navigationBarColor
        }
    }
    
    class var backgroundColor: UIColor {
        get {
            return theme.backgroundColor
        }
    }
    
    class var backgroundHighlightColor: UIColor {
        get {
            return theme.backgroundHighlightColor
        }
    }
    
    class var tabBackgroundColor: UIColor {
        get {
            return theme.tabBackgroundColor
        }
    }
    
    class var tabIconColor: UIColor {
        get {
            return theme.tabIconColor
        }
    }
    
    class var tabIconSelectedColor: UIColor {
        get {
            return theme.tabIconSelectedColor
        }
    }
    
    class var contrastAccentColor: UIColor {
        get {
            return theme.contrastAccentColor
        }
    }
    
    class var alwaysDarkColor: UIColor {
        get {
            return .fromRgb(0x17191B)
        }
    }
    
    class var lightAccentColor: UIColor {
        get {
            return theme.lightAccentColor
        }
    }
    
    class var shadowAccentColor: UIColor {
        get {
            return theme.shadowAccentColor
        }
    }
}

extension UIBarButtonItem {
    public static func imageButton(named: String, onClick: @escaping () -> Void) -> UIBarButtonItem {
        let imageView = UIImageView()
        
        imageView.image = UIImage(named: named)
        
        return UIBarButtonItem(customView: imageView.onTouch {
            onClick()
        })
    }
}

public extension UIStackView {
    @discardableResult
    func makeVertical() -> Self {
        self.axis = .vertical
        
        return self
    }
    
    @discardableResult
    func setDistribution(_ distribution: UIStackView.Distribution) -> Self {
        self.distribution = distribution
        
        return self
    }
    
    @discardableResult
    func setAlignment(_ alignment: UIStackView.Alignment) -> Self {
        self.alignment = alignment
        
        return self
    }
    
    @discardableResult
    func setSpacing(_ spacing: CGFloat) -> Self {
        self.spacing = spacing
        
        return self
    }
}

public extension UITextField {
    @discardableResult
    func onTextChanged(_ changeHandler: @escaping (String) -> Void) -> Self {
        let target = UITextFieldChangeCallback(changeHandler)

        self.addTarget(target, action: #selector(UITextFieldChangeCallback.invoke), for: .editingChanged)

        objc_setAssociatedObject(self, &UITextFieldChangeKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)

        return self
    }
    
    @discardableResult
    func onTextChangedAsync(_ changeHandler: @escaping (String, @escaping (Error?) -> Void) -> Void) -> Self {
        let target = UITextFieldChangeCallback({ text in
            changeHandler(text) { _ in
            }
        })

        self.addTarget(target, action: #selector(UITextFieldChangeCallback.invoke), for: .editingChanged)

        objc_setAssociatedObject(self, &UITextFieldChangeKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)

        return self
    }
}

@objc class UITextViewTextChangeDelegate : NSObject, UITextViewDelegate {
    private var callback: [(String) -> Void] = []
    
    init(callback: @escaping (String) -> Void) {
        self.callback = [callback]
    }
    
    func addCallback(_ callback: @escaping (String) -> Void) {
        self.callback.append(callback)
    }
    
    func textViewDidChange(_ textView: UITextView) {
        for cb in self.callback {
            cb(textView.text)
        }
    }
}

@objc class UISegmentedControlValueChangeDelegate : NSObject {
    private let callback: (NSInteger) -> Void
    
    init(callback: @escaping (NSInteger) -> Void) {
        self.callback = callback
    }
    
    @objc func valueChanged(_ control: UISegmentedControl) {
        self.callback(control.selectedSegmentIndex)
    }
}

public extension UISegmentedControl {
    @discardableResult
    func onValuedChanged(_ changeHandler: @escaping (NSInteger) -> Void) -> Self {
        let target = UISegmentedControlValueChangeDelegate(callback: changeHandler)

        self.addTarget(target, action: #selector(UISegmentedControlValueChangeDelegate.valueChanged), for: .valueChanged)

        objc_setAssociatedObject(self, &UITextFieldChangeKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        
        return self
    }
}

public extension UITextView {
    @discardableResult
    func onTextChanged(_ changeHandler: @escaping (String) -> Void) -> Self {
        var target = objc_getAssociatedObject(self, &UITextFieldChangeKey) as? UITextViewTextChangeDelegate
        
        if target != nil {
            target?.addCallback(changeHandler)
        } else {
            target = UITextViewTextChangeDelegate(callback: changeHandler)
            
            self.delegate = target
            
            objc_setAssociatedObject(self, &UITextFieldChangeKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
        
        return self
    }
    
    @discardableResult
    func onTextChangedAsync(_ changeHandler: @escaping (String, @escaping (Error?) -> Void) -> Void) -> Self {
        var target = objc_getAssociatedObject(self, &UITextFieldChangeKey) as? UITextViewTextChangeDelegate
        
        if target != nil {
            target?.addCallback { text in
                changeHandler(text) { _ in
                }
            }
        } else {
            target = UITextViewTextChangeDelegate(callback: { text in
                changeHandler(text) { _ in
                }
            })
            
            self.delegate = target
            
            objc_setAssociatedObject(self, &UITextFieldChangeKey, target, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
        
        return self
    }
}

public extension UIApplication {

    func clearLaunchScreenCache() {
        do {
            try FileManager.default.removeItem(atPath: NSHomeDirectory()+"/Library/SplashBoard")
        } catch {
            print("Failed to delete launch screen cache: \(error)")
        }
    }

}