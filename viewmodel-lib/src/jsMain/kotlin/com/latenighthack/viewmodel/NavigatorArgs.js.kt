package com.latenighthack.viewmodel

import com.latenighthack.viewmodel.common.fromBase64String
import com.latenighthack.viewmodel.common.toBase64String
import kotlin.reflect.KProperty

public actual open class NavigatorArgs actual constructor() {
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

public fun paramToString(param: dynamic): String = (param as? String) ?: ""
public fun paramToByteArray(param: dynamic): ByteArray {
    val paramOrEmpty = (param as? String) ?: ""

    if (paramOrEmpty.isNullOrEmpty()) {
        return byteArrayOf()
    } else {
        return paramOrEmpty.fromBase64String()
    }
}

public fun paramToInt(param: dynamic): Int = ((param as? String) ?: "").toInt()
public fun paramToBoolean(param: dynamic): Boolean = ((param as? String) ?: "").toBoolean()

public fun argToParam(arg: String): String = arg
public fun argToParam(arg: ByteArray): String = arg.toBase64String() ?: ""
public fun argToParam(arg: Int): String = arg.toString()
public fun argToParam(arg: Boolean): String = if (arg) "true" else "false"
