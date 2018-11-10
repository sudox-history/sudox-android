package com.sudox.design.behaviours

import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.view.View

class ProfileAvatarBehaviour<V: View>: CoordinatorLayout.Behavior<V>() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        return dependency is Toolbar
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        return super.onDependentViewChanged(parent, child, dependency)
    }
}