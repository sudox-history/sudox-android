package ru.sudox.android.core

import com.bumptech.glide.manager.RequestManagerTreeNode
import com.lalafo.conductor.glide.BaseGlideControllerSupport
import com.lalafo.conductor.glide.ControllerLifecycle
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.Images

class CoreGlideControllerSupport(
        val coreController: CoreController
) : BaseGlideControllerSupport<GlideRequests>(coreController) {

    override fun getGlideRequest(lifecycle: ControllerLifecycle, requestManagerTreeNode: RequestManagerTreeNode?): GlideRequests {
        val context = coreController.applicationContext!!
        val glide = Images.get(coreController.applicationContext!!)

        return GlideRequests(glide, lifecycle, requestManagerTreeNode!!, context)
    }
}