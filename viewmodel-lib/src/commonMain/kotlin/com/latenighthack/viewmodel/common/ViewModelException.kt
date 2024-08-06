package com.latenighthack.viewmodel.common

public class ViewModelException(
    cause: Throwable,
    public val title: String = "Error",
    override val message: String = cause.message ?: "",
) : Exception(cause) {
    public constructor(message: String): this(Throwable(message), message)
    public constructor(title: String, message: String): this(Throwable(message), title, message)
}
