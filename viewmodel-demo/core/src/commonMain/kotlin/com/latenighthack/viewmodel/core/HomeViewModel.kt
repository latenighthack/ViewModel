package com.latenighthack.viewmodel.core

import com.latenighthack.viewmodel.NavigableViewModel
import com.latenighthack.viewmodel.NavigatorArgs
import com.latenighthack.viewmodel.StatefulViewModel
import com.latenighthack.viewmodel.ViewModel
import com.latenighthack.viewmodel.annotations.DeclareViewModel
import com.latenighthack.viewmodel.annotations.DeclareViewModelList
import com.latenighthack.viewmodel.list.Delta
import com.latenighthack.viewmodel.list.flowListOf
import kotlinx.coroutines.flow.Flow

interface IHomeListItemViewModel {
}

@DeclareViewModel
interface IHomeListItemAViewModel: IHomeListItemViewModel, ViewModel<IHomeListItemAViewModel.State> {
    data class State(val titleA: String)
}

@DeclareViewModel
interface IHomeListItemBViewModel: IHomeListItemViewModel, ViewModel<IHomeListItemBViewModel.State> {
    data class State(val titleB: String)
}

@DeclareViewModel
interface IHomeViewModel: NavigableViewModel<IHomeViewModel.State, IHomeViewModel.Args> {
    data class State(val text: String)

    class Args : NavigatorArgs()

    @DeclareViewModelList(
        IHomeListItemAViewModel::class,
        IHomeListItemBViewModel::class
    )
    val items: Flow<Delta<IHomeListItemViewModel>>

    suspend fun onStartTapped()
}

class HomeListItemAViewModel(title: String) : IHomeListItemAViewModel, StatefulViewModel<IHomeListItemAViewModel.State>(IHomeListItemAViewModel.State(title))
class HomeListItemBViewModel(title: String) : IHomeListItemBViewModel, StatefulViewModel<IHomeListItemBViewModel.State>(IHomeListItemBViewModel.State(title))

class HomeViewModel(
    override val args: IHomeViewModel.Args
) : IHomeViewModel, StatefulViewModel<IHomeViewModel.State>(IHomeViewModel.State("Hello world!")) {

    override suspend fun onStartTapped() {
        update {
            copy(text = "Updated World!")
        }
    }

    override val items: Flow<Delta<IHomeListItemViewModel>> = flowListOf(
        HomeListItemAViewModel("Item A1"),
        HomeListItemAViewModel("Item A2"),
        HomeListItemBViewModel("Item B1"),
        HomeListItemBViewModel("Item B2"),
        HomeListItemBViewModel("Item B3"),
    )
}
