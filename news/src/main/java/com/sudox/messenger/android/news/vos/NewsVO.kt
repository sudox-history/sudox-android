package com.sudox.messenger.android.news.vos

data class NewsVO(
        val publisherName: Int,
        val publisherPhoto: Int,
        val publishTime: Long,
        val commentsCount: Long,
        val repostsCount: Long,
        val rating: Long,
        val content: String?
)