package com.github.jetbrains.rssreader.domain

import kotlinx.serialization.Serializable

@Serializable
data class SavedPost(
    val link: String,
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val pubDate: String?
)

fun Item.toSavedPost() = SavedPost(
    link = link ?: guid,
    title = title,
    description = description,
    imageUrl = getImageUrl(),
    pubDate = pubDate
)
