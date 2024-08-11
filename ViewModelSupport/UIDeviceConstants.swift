//
//  UIDeviceConstants.swift
//  design
//
//  Created by Mike Roberts on 2023-08-30.
//

import UIKit

public enum SupportedDeviceType {
    case unknown
    case ipod_touch_5g
    case ipod_touch_6g
    case ipod_touch_7g
    case iphone_4
    case iphone_4s
    case iphone_5
    case iphone_5c
    case iphone_5s
    case iphone_6
    case iphone_6_plus
    case iphone_6s
    case iphone_6s_plus
    case iphone_7
    case iphone_7_plus
    case iphone_8
    case iphone_8_plus
    case iphone_x
    case iphone_xs
    case iphone_xs_max
    case iphone_xr
    case iphone_11
    case iphone_11_pro
    case iphone_11_pro_max
    case iphone_12_mini
    case iphone_12
    case iphone_12_pro
    case iphone_12_pro_max
    case iphone_13_mini
    case iphone_13
    case iphone_13_pro
    case iphone_13_pro_max
    case iphone_14
    case iphone_14_plus
    case iphone_14_pro
    case iphone_14_pro_max
    case iphone_15
    case iphone_15_plus
    case iphone_15_pro
    case iphone_15_pro_max
    case iphone_se
    case iphone_se_2g
    case iphone_se_3g
    case ipad_2
    case ipad_3g
    case ipad_4g
    case ipad_5g
    case ipad_6g
    case ipad_7g
    case ipad_8g
    case ipad_9g
    case ipad_10g
    case ipad_air
    case ipad_air2
    case ipad_air_3g
    case ipad_air_4g
    case ipad_air_5g
    case ipad_mini
    case ipad_mini_2
    case ipad_mini_3
    case ipad_mini_4
    case ipad_mini_5g
    case ipad_mini_6g
    case ipad_pro_9
    case ipad_pro_10
    case ipad_pro_11
    case ipad_pro_11_2g
    case ipad_pro_11_3g
    case ipad_pro_11_4g
    case ipad_pro_13
    case ipad_pro_13_2g
    case ipad_pro_13_3g
    case ipad_pro_13_4g
    case ipad_pro_13_5g
    case ipad_pro_13_6g
    case apple_tv
    case apple_tv_4k
    case homepod
    case homepod_mini
}

public extension UIDevice {
    static let supportedDeviceType: SupportedDeviceType = {
        var systemInfo = utsname()
        uname(&systemInfo)
        let machineMirror = Mirror(reflecting: systemInfo.machine)
        let identifier = machineMirror.children.reduce("") { identifier, element in
            guard let value = element.value as? Int8, value != 0 else { return identifier }
            return identifier + String(UnicodeScalar(UInt8(value)))
        }

        func mapToDevice(identifier: String) -> SupportedDeviceType { // swiftlint:disable:this cyclomatic_complexity
            switch identifier {
            case "iPod5,1":                                       return .ipod_touch_5g
            case "iPod7,1":                                       return .ipod_touch_6g
            case "iPod9,1":                                       return .ipod_touch_7g
            case "iPhone3,1", "iPhone3,2", "iPhone3,3":           return .iphone_4
            case "iPhone4,1":                                     return .iphone_4s
            case "iPhone5,1", "iPhone5,2":                        return .iphone_5
            case "iPhone5,3", "iPhone5,4":                        return .iphone_5c
            case "iPhone6,1", "iPhone6,2":                        return .iphone_5s
            case "iPhone7,2":                                     return .iphone_6
            case "iPhone7,1":                                     return .iphone_6_plus
            case "iPhone8,1":                                     return .iphone_6s
            case "iPhone8,2":                                     return .iphone_6s_plus
            case "iPhone9,1", "iPhone9,3":                        return .iphone_7
            case "iPhone9,2", "iPhone9,4":                        return .iphone_7_plus
            case "iPhone10,1", "iPhone10,4":                      return .iphone_8
            case "iPhone10,2", "iPhone10,5":                      return .iphone_8_plus
            case "iPhone10,3", "iPhone10,6":                      return .iphone_x
            case "iPhone11,2":                                    return .iphone_xs
            case "iPhone11,4", "iPhone11,6":                      return .iphone_xs_max
            case "iPhone11,8":                                    return .iphone_xr
            case "iPhone12,1":                                    return .iphone_11
            case "iPhone12,3":                                    return .iphone_11_pro
            case "iPhone12,5":                                    return .iphone_11_pro_max
            case "iPhone13,1":                                    return .iphone_12_mini
            case "iPhone13,2":                                    return .iphone_12
            case "iPhone13,3":                                    return .iphone_12_pro
            case "iPhone13,4":                                    return .iphone_12_pro_max
            case "iPhone14,4":                                    return .iphone_13_mini
            case "iPhone14,5":                                    return .iphone_13
            case "iPhone14,2":                                    return .iphone_13_pro
            case "iPhone14,3":                                    return .iphone_13_pro_max
            case "iPhone14,7":                                    return .iphone_14
            case "iPhone14,8":                                    return .iphone_14_plus
            case "iPhone15,2":                                    return .iphone_14_pro
            case "iPhone15,3":                                    return .iphone_14_pro_max
            case "iPhone15,4":                                    return .iphone_15
            case "iPhone15,5":                                    return .iphone_15_plus
            case "iPhone16,1":                                    return .iphone_15_pro
            case "iPhone16,2":                                    return .iphone_15_pro_max
            case "iPhone8,4":                                     return .iphone_se
            case "iPhone12,8":                                    return .iphone_se_2g
            case "iPhone14,6":                                    return .iphone_se_3g
            case "iPad2,1", "iPad2,2", "iPad2,3", "iPad2,4":      return .ipad_2
            case "iPad3,1", "iPad3,2", "iPad3,3":                 return .ipad_3g
            case "iPad3,4", "iPad3,5", "iPad3,6":                 return .ipad_4g
            case "iPad6,11", "iPad6,12":                          return .ipad_5g
            case "iPad7,5", "iPad7,6":                            return .ipad_6g
            case "iPad7,11", "iPad7,12":                          return .ipad_7g
            case "iPad11,6", "iPad11,7":                          return .ipad_8g
            case "iPad12,1", "iPad12,2":                          return .ipad_9g
            case "iPad13,18", "iPad13,19":                        return .ipad_10g
            case "iPad4,1", "iPad4,2", "iPad4,3":                 return .ipad_air
            case "iPad5,3", "iPad5,4":                            return .ipad_air2
            case "iPad11,3", "iPad11,4":                          return .ipad_air_3g
            case "iPad13,1", "iPad13,2":                          return .ipad_air_4g
            case "iPad13,16", "iPad13,17":                        return .ipad_air_5g
            case "iPad2,5", "iPad2,6", "iPad2,7":                 return .ipad_mini
            case "iPad4,4", "iPad4,5", "iPad4,6":                 return .ipad_mini_2
            case "iPad4,7", "iPad4,8", "iPad4,9":                 return .ipad_mini_3
            case "iPad5,1", "iPad5,2":                            return .ipad_mini_4
            case "iPad11,1", "iPad11,2":                          return .ipad_mini_5g
            case "iPad14,1", "iPad14,2":                          return .ipad_mini_6g
            case "iPad6,3", "iPad6,4":                            return .ipad_pro_9
            case "iPad7,3", "iPad7,4":                            return .ipad_pro_10
            case "iPad8,1", "iPad8,2", "iPad8,3", "iPad8,4":      return .ipad_pro_11
            case "iPad8,9", "iPad8,10":                           return .ipad_pro_11_2g
            case "iPad13,4", "iPad13,5", "iPad13,6", "iPad13,7":  return .ipad_pro_11_3g
            case "iPad14,3", "iPad14,4":                          return .ipad_pro_11_4g
            case "iPad6,7", "iPad6,8":                            return .ipad_pro_13
            case "iPad7,1", "iPad7,2":                            return .ipad_pro_13_2g
            case "iPad8,5", "iPad8,6", "iPad8,7", "iPad8,8":      return .ipad_pro_13_3g
            case "iPad8,11", "iPad8,12":                          return .ipad_pro_13_4g
            case "iPad13,8", "iPad13,9", "iPad13,10", "iPad13,11":return .ipad_pro_13_5g
            case "iPad14,5", "iPad14,6":                          return .ipad_pro_13_6g
            case "AppleTV5,3":                                    return .apple_tv
            case "AppleTV6,2":                                    return .apple_tv_4k
            case "AudioAccessory1,1":                             return .homepod
            case "AudioAccessory5,1":                             return .homepod_mini
            case "i386", "x86_64", "arm64":                       return mapToDevice(identifier: ProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"] ?? "iOS")
            default:                                              return .unknown
            }
        }

        return mapToDevice(identifier: identifier)
    }()

}

public struct UIDeviceConstants {
    public static let statusBarHeight = {
        switch UIDevice.supportedDeviceType {
        case .iphone_se: fallthrough
        case .iphone_se_2g: fallthrough
        case .iphone_se_3g:
            return 20.0
        default:
            return 52.0
        }
    }()
    
    public static let cornerRadius = {
        switch UIDevice.supportedDeviceType {
        case .iphone_14: fallthrough
        case .iphone_14_plus: fallthrough
        case .iphone_14_pro: fallthrough
        case .iphone_14_pro_max: fallthrough
        case .iphone_15: fallthrough
        case .iphone_15_plus: fallthrough
        case .iphone_15_pro: fallthrough
        case .iphone_15_pro_max:
            return 55.0
        case .iphone_se: fallthrough
        case .iphone_se_2g: fallthrough
        case .iphone_se_3g:
            return 8.0
        default:
            return 22.0
        }
    }()
    
    public static let isStubbyScreen = {
        switch UIDevice.supportedDeviceType {
        case .iphone_se: fallthrough
        case .iphone_se_2g: fallthrough
        case .iphone_se_3g:
            return true
        default:
            return false
        }
    }()
}
