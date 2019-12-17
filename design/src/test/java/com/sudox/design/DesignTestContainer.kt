package com.sudox.design

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

class DesignTestContainer<T : View>(
        val viewCreator: (Context) -> (T)
) {

    private var controller: ActivityController<AppCompatActivity>? = null
    private var activity: Activity? = null
    private var view: T? = null

    fun fill(): T {
        val oldView = view
        var state: Bundle? = null

        if (controller != null) {
            state = Bundle()

            controller!!.saveInstanceState(state)
            controller!!.pause()
            controller!!.stop()
            controller!!.destroy()
        }

        controller = Robolectric
                .buildActivity(AppCompatActivity::class.java)
                .create()
                .start()

        activity = controller!!.get()
        view = viewCreator(activity!!)

        if (oldView != null) {
            view!!.id = oldView.id
        }

        activity!!.setContentView(view)

        if (state != null) {
            controller!!.restoreInstanceState(state)
        }

        controller!!
                .resume()
                .visible()

        return view!!
    }
}