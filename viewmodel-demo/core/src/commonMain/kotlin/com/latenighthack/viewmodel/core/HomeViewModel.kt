package com.latenighthack.viewmodel.core

import com.latenighthack.api.v1.DummyService
import com.latenighthack.api.v1.GetDummyRequest
import com.latenighthack.ktcrypto.ECDH
import com.latenighthack.ktcrypto.PublicKey
import com.latenighthack.ktcrypto.RNG
import com.latenighthack.ktcrypto.Secp256r1
import com.latenighthack.ktcrypto.Secp256r1KeyPair
import com.latenighthack.ktcrypto.Secp256r1PublicKey
import com.latenighthack.ktcrypto.encode
import com.latenighthack.ktcrypto.generate
import com.latenighthack.ktcrypto.randomBytes
import com.latenighthack.ktcrypto.tools.toBase64String
import com.latenighthack.storage.v1.StoredProperty
import com.latenighthack.viewmodel.NavigableViewModel
import com.latenighthack.viewmodel.NavigatorArgs
import com.latenighthack.viewmodel.StatefulViewModel
import com.latenighthack.viewmodel.ViewModel
import com.latenighthack.viewmodel.annotations.DeclareViewModel
import com.latenighthack.viewmodel.annotations.DeclareViewModelList
import com.latenighthack.viewmodel.core.store.SimpleStore
import com.latenighthack.viewmodel.list.Delta
import com.latenighthack.viewmodel.list.flowListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

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
    override val args: IHomeViewModel.Args,
    val basicStore: SimpleStore,
    val service: DummyService
) : IHomeViewModel, StatefulViewModel<IHomeViewModel.State>(IHomeViewModel.State("Hello world!")) {

    override suspend fun onStartTapped() {
//        val response = service.getDummy(GetDummyRequest {
//            value = 123
//        })

        update {
            val id = RNG.randomBytes(8)

            basicStore.saveProperty(StoredProperty {
                id {
                    rawValue = id
                }
                description = id.toBase64String()
            })

            copy(text = "Updated World!")
        }
    }

    override val items: Flow<Delta<IHomeListItemViewModel>> = flowListOf<IHomeListItemViewModel> {
//        val key = Secp256r1KeyPair.generate()

        basicStore.getAllProperties().map {
            HomeListItemAViewModel(it.description)
        } + listOf(
            HomeListItemBViewModel("Test ${RNG.randomBytes(8).toBase64String()}"),
            HomeListItemBViewModel("Test ${RNG.randomBytes(8).toBase64String()}"),
//            HomeListItemBViewModel("Pub Key ${key.publicKey.encode().toBase64String()}"),
//            HomeListItemBViewModel("Priv Key ${key.privateKey.encode().toBase64String()}")
        )
    }
}
