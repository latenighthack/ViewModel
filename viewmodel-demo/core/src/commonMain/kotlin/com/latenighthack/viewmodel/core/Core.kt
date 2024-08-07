package com.latenighthack.viewmodel.core

import com.latenighthack.ktstore.StoreDelegate
import com.latenighthack.viewmodel.core.store.SimpleStore

class Core(
    private val delegate: StoreDelegate
) {
    val simpleStore: SimpleStore = SimpleStore(delegate)

    suspend fun start() {
        simpleStore.prepare()

        delegate.createStores()
    }
}
