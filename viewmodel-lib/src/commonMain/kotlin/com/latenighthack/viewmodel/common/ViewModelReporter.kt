package com.latenighthack.viewmodel.common

public interface ViewModelReporter {
    public fun trackNavigation(screen: String)

    public fun trackAction(
        screen: String,
        parent: String,
        noun: String,
        verb: String,
        success: Boolean,
        duration: Long?,
        error: Throwable?
    )
}
