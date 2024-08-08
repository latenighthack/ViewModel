//
//  AmplitudeViewModelReporter.swift
//  app
//
//  Created by Mike Roberts on 2024-08-08.
//

import UIKit
import ViewModelSupport
import Core

class AmplitudeViewModelReporter: Viewmodel_libViewModelReporter {
    func trackAction(screen: String, parent: String, noun: String, verb: String, success: Bool, duration: KotlinLong?, error: KotlinThrowable?) {
    }
    
    func trackNavigation(screen: String) {
    }
}
