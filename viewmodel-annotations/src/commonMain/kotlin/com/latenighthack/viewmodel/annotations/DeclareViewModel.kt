package com.latenighthack.viewmodel.annotations

import kotlin.reflect.KClass

public annotation class Extra(val key: String, val value: String)

public object ScreenTypeConstants {
    public const val User: String = "USER"
    public const val Hero: String = "HERO"
    public const val List: String = "LIST"
}
public object PropertyConstants {
    public const val Search: String = "SEARCH"
}

public annotation class Extras(vararg val extras: Extra) {
    public companion object {
        public val PropertyKey: String = "PropertyKey"
        public val ScreenTypeKey: String = "ScreenTypeKey"
        public val ScreenType: ScreenTypeConstants = ScreenTypeConstants
    }
}

@Target(AnnotationTarget.CLASS)
public annotation class DeclareViewModel(val webPath: String = "", val extras: Extras = Extras())

@Target(AnnotationTarget.PROPERTY)
public annotation class DeclareRouteArg(val extras: Extras = Extras())

@Target(AnnotationTarget.FUNCTION)
public annotation class DeclareNavigation(vararg val targets: KClass<*>, val extras: Extras = Extras())

@Target(AnnotationTarget.PROPERTY)
public annotation class DeclareChildViewModel(val extras: Extras = Extras())

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
public annotation class CodegenIgnore(val extras: Extras = Extras())

@Target(AnnotationTarget.PROPERTY)
public annotation class DeclareViewModelList(vararg val possibleTypes: KClass<*>, val extras: Extras = Extras())
