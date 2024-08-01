package com.latenighthack.viewmodel

import kotlin.reflect.KProperty

public interface StoredProperty<T> {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T

    public operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public expect open class NavigatorArgs constructor() {
    protected inline fun <reified T> storedProperty(): StoredProperty<T>
}
