package com.latenighthack.viewmodel.test.navigator

import com.latenighthack.viewmodel.NavigatorArgs

public interface TestNavigator {
    public fun reportNavigation(args: NavigatorArgs)

    public suspend fun <Value> reportNavigationCallback(args: NavigatorArgs): Value

    public fun reportClose()
}
