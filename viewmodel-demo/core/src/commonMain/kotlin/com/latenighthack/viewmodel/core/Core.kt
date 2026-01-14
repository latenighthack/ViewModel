package com.latenighthack.viewmodel.core

import com.latenighthack.api.v1.DummyService
import com.latenighthack.api.v1.DummyServiceRpc
import com.latenighthack.ktbuf.rpc.HttpRpcClient
import com.latenighthack.ktstore.StoreDelegate
import com.latenighthack.ktstore.createStoreDelegate
import com.latenighthack.viewmodel.core.store.SimpleStore

interface NavigatorModule {
    val navigator: Navigator
}

class Core(private val serverPath: String) {
    private val delegate: StoreDelegate = createStoreDelegate("main_db")
    private val httpClient = HttpRpcClient(serverPath)

    val dummyService: DummyService = DummyServiceRpc(httpClient)
    val simpleStore: SimpleStore = SimpleStore(delegate)

    suspend fun start() {
        simpleStore.prepare()

        delegate.createStores()
    }
}
