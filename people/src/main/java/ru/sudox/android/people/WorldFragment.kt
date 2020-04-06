package ru.sudox.android.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.sudox.android.core.CoreFragment

class WorldFragment : CoreFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        screenManager!!.reset()

        return inflater.inflate(R.layout.fragment_world, container, false)
    }
}