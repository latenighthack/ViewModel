package com.latenighthack.viewmodel.core

public interface Navigator {
    fun close()

    fun navigateTo(home: IHomeViewModel.Args)
}
