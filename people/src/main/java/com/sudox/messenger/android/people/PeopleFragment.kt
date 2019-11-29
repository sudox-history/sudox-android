package com.sudox.messenger.android.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import kotlinx.android.synthetic.main.fragment_people.peopleButton

class PeopleFragment : CoreFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as CoreActivity).getScreenManager().reset()

        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        peopleButton.setOnClickListener {
            (activity as CoreActivity)
                    .getNavigationManager()
                    .showChildFragment(ChildFragment())
        }
    }
}