package com.latenighthack.viewmodel

class ViewModelOptions(options: Map<String, String>) {
    /**
     * 0: Turn off all ViewModel related error checking
     *
     * 1: Check for errors
     *
     * 2: Turn errors into warnings
     */
    val errorsLoggingType: Int = (options["ViewModel_Errors"]?.toIntOrNull()) ?: 1

    /**
     * If set to true, the generated code will contain qualified type names
     */
    val setQualifiedType = options["ViewModel_QualifiedTypeName"]?.toBoolean() ?: false

    val navigatorClassName = options["ViewModel_NavigatorClassName"]?.toString() ?: "com.latenighthack.viewmodel.Navigator"
    val resolverClassName = options["ViewModel_ResolverClassName"]?.toString() ?: "com.latenighthack.viewmodel.Core"
}
