package com.latenighthack.viewmodel.common

public data class ProfilePhotoModel(
    val elements: List<ProfilePhotoElementModel>,
    val extraCount: Int,
    val isOneToOne: Boolean
) {
    public companion object {
        public val empty: ProfilePhotoModel = ProfilePhotoModel(emptyList(), 0, true)
        public fun single(url: String?, placeholderColor: Int = 0xffffffff.toInt()): ProfilePhotoModel = ProfilePhotoModel(
            listOf(ProfilePhotoElementModel(url, placeholderColor)),
            0,
            true
        )
    }
}

public data class ProfilePhotoElementModel(
    val url: String?,
    val placeholderColor: Int
)
