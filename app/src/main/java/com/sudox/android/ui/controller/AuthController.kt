package com.sudox.android.ui.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.sudox.android.R

class AuthController : Controller() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        return view!!
    }
}