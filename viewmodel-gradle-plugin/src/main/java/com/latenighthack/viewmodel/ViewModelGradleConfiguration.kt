package com.latenighthack.viewmodel

open class ViewModelGradleConfiguration {
//    var generateQualifiedTypeName: Boolean = false
//    var errorCheckingMode: ErrorCheckingMode = ErrorCheckingMode.ERROR
    var navigatorClassName: String = ""
    var resolverClassName: String = ""
}

enum class ErrorCheckingMode {
    /**
     * Turn off all ViewModel related error checking
     */
    NONE,

    /**
     * Check for errors
     */
    ERROR,

    /**
     * Turn errors into warnings
     */
    WARNING,
}
