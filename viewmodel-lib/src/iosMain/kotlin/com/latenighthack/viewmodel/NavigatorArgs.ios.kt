package com.latenighthack.viewmodel

import kotlin.reflect.KProperty

/**
 * iOS doesn't need anything particularly fancy for navigation args as arguments don't need
 * to be serialized. They can cross the in-memory threshold intact
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual open class NavigatorArgs {
    protected class InMemoryStoredProperty<T> : StoredProperty<T> {
        private var storedValue: T? = null

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return storedValue!!
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            storedValue = value
        }
    }

    protected actual inline fun <reified T> storedProperty(): StoredProperty<T> =
        InMemoryStoredProperty()
}
