package com.latenighthack.viewmodel.core

interface Navigator {
    fun close()

    fun navigateTo(home: IHomeViewModel.Args)
}
