package com.latenighthack.viewmodel.core.store

import com.latenighthack.ktstore.Store
import com.latenighthack.ktstore.StoreDelegate
import com.latenighthack.storage.v1.*

class SimpleStore(delegate: StoreDelegate) : Store<StoredProperty>(
    delegate,
    "simple",
    StoredProperty::toByteArray,
    StoredProperty.Companion::fromByteArray
) {
    private val id = serializedIndex(StoredProperty::id, StoredId::toByteArray)
        .also { primaryKey(it) }

    suspend fun getAllProperties(): List<StoredProperty> = getAll()
    suspend fun saveProperty(property: StoredProperty) = save(property)
}
