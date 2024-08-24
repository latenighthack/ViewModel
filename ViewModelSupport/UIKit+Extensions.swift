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

fileprivate extension UIImage {
    static func button(named: String) -> UIImage {
        return UIImage(named: named)!
            .resizableImage(withCapInsets: .init(top: 20.0, left: 20.0, bottom: 20.0, right: 20.0), resizingMode: .stretch)
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
    func constrainEdges(toEdgesOf view: UIView, insetBy insets: UIEdgeInsets = .zero, useSafeArea: Bool = false) -> Self {
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
    static func constrainVerticalStack(
        ofViews views: [UIView],
        separatedBy separation: CGFloat = 0.0,
        startAt: NSLayoutAnchor<NSLayoutYAxisAnchor>? = nil,
        endAt: NSLayoutAnchor<NSLayoutYAxisAnchor>? = nil
    ) -> [NSLayoutConstraint] {
        var constraints = [NSLayoutConstraint]()
        var previousConstraint: NSLayoutAnchor<NSLayoutYAxisAnchor>? = startAt
        var previousView: UIView? = nil
        var nextSeparation = 0.0

        for view in views {
            if let previousConstraint = previousConstraint {
                constraints.append(view.topAnchor.constraint(
                    equalTo: previousConstraint,
                    constant: nextSeparation
                ))
            }

            nextSeparation = separation
            previousView = view
            previousConstraint = view.bottomAnchor
        }

        if let endConstraint = endAt, let previousView = previousView {
            constraints.append(previousView.bottomAnchor.constraint(equalTo: endConstraint))
        }

        NSLayoutConstraint.activate(constraints)

        return constraints
    }

    @discardableResult
    static func constrainHorizontalStack(
        ofViews views: [UIView],
        separatedBy separation: CGFloat = 0.0,
        startAt: NSLayoutAnchor<NSLayoutXAxisAnchor>? = nil,
        endAt: NSLayoutAnchor<NSLayoutXAxisAnchor>? = nil
    ) -> [NSLayoutConstraint] {
        var constraints = [NSLayoutConstraint]()
        var previousConstraint: NSLayoutAnchor<NSLayoutXAxisAnchor>? = startAt
        var previousView: UIView? = nil
        var nextSeparation = 0.0

        for view in views {
            if let previousConstraint = previousConstraint {
                constraints.append(view.leadingAnchor.constraint(
                    equalTo: previousConstraint,
                    constant: nextSeparation
                ))
            }

            nextSeparation = separation
            previousView = view
            previousConstraint = view.trailingAnchor
        }

        if let endConstraint = endAt, let previousView = previousView {
            constraints.append(previousView.trailingAnchor.constraint(equalTo: endConstraint))
        }

        NSLayoutConstraint.activate(constraints)

        return constraints
    }

    enum UseLayoutGuide {
        case standard
        case safeArea
        case margins
        case readable
    }

    @discardableResult
    func constrainHorizontal(
        toHorizontalOf view: UIView,
        insetLeadingBy insetLeading: CGFloat = 0.0,
        insetTrailingBy insetTrailing: CGFloat = 0.0,
        useGuide: UseLayoutGuide = .standard
    ) -> Self {
        var layoutGuide: UILayoutGuide? = nil

        switch useGuide {
        case .standard:
            layoutGuide = nil
        case .safeArea:
            layoutGuide = view.safeAreaLayoutGuide
        case .margins:
            layoutGuide = view.layoutMarginsGuide
        case .readable:
            layoutGuide = view.readableContentGuide
        }

        let leadingAnchor = layoutGuide?.leadingAnchor ?? view.leadingAnchor
        let trailingAnchor = layoutGuide?.trailingAnchor ?? view.trailingAnchor

        NSLayoutConstraint.activate([
            self.leadingAnchor.constraint(equalTo: leadingAnchor, constant: insetLeading),
            self.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -insetTrailing)
        ])
        return self
    }

    @discardableResult
    func constrainVertical(
        toVerticalOf view: UIView,
        insetTopBy insetTop: CGFloat = 0.0,
        insetBottomBy insetBottom: CGFloat = 0.0,
        useGuide: UseLayoutGuide = .standard
    ) -> Self {
        var layoutGuide: UILayoutGuide? = nil

        switch useGuide {
        case .standard:
            layoutGuide = nil
        case .safeArea:
            layoutGuide = view.safeAreaLayoutGuide
        case .margins:
            layoutGuide = view.layoutMarginsGuide
        case .readable:
            layoutGuide = view.readableContentGuide
        }

        let topAnchor = layoutGuide?.topAnchor ?? view.topAnchor
        let bottomAnchor = layoutGuide?.bottomAnchor ?? view.bottomAnchor

        NSLayoutConstraint.activate([
            self.topAnchor.constraint(equalTo: topAnchor, constant: insetTop),
            self.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -insetBottom)
        ])
        return self
    }

    @discardableResult
    func constrainWidth(toWidthOf view: UIView, insetBy inset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let widthAnchor = useSafeArea ? view.safeAreaLayoutGuide.widthAnchor : view.widthAnchor
        NSLayoutConstraint.activate([
            self.widthAnchor.constraint(equalTo: widthAnchor, constant: -inset * 2.0)
        ])
        return self
    }

    @discardableResult
    func constrainHeight(toHeightOf view: UIView, insetBy inset: CGFloat = 0.0, useSafeArea: Bool = false) -> Self {
        let heightAnchor = useSafeArea ? view.safeAreaLayoutGuide.heightAnchor : view.heightAnchor
        NSLayoutConstraint.activate([
            self.heightAnchor.constraint(equalTo: heightAnchor, constant: -inset * 2.0)
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
