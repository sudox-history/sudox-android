package com.sudox.design.attachmentsLayout.vos

import android.graphics.Bitmap

data class LinkAttachmentVO(
        val description: String,
        val contentImage: Bitmap,
        val publisherIcon: Bitmap,
        val link: String
)