package com.latenighthack.viewmodel.annotations

import kotlin.reflect.KClass

public annotation class Extra(val key: String, val value: String)

public annotation class Extras(vararg val extras: Extra)

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
