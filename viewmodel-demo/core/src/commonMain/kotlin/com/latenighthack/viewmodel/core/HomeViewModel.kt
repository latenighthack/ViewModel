package com.latenighthack.viewmodel.core

import com.latenighthack.viewmodel.NavigableViewModel
import com.latenighthack.viewmodel.NavigatorArgs
import com.latenighthack.viewmodel.StatefulViewModel
import com.latenighthack.viewmodel.annotations.DeclareViewModel

@DeclareViewModel
interface IHomeViewModel: NavigableViewModel<IHomeViewModel.State, IHomeViewModel.Args> {
    data class State(val text: String)

    class Args : NavigatorArgs()

    suspend fun onStartTapped()
}

class HomeViewModel(
    override val args: IHomeViewModel.Args
) : IHomeViewModel, StatefulViewModel<IHomeViewModel.State>(IHomeViewModel.State("Hello world!")) {

    override suspend fun onStartTapped() {
        update {
            copy(text = "Updated World!")
        }
    }
}
