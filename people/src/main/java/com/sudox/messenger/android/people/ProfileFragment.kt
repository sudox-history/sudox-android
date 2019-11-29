package com.sudox.messenger.android.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment

class ProfileFragment : CoreFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as CoreActivity).getScreenManager().reset()

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
}