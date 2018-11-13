package com.sudox.android.ui.main.contacts.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sudox.android.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_add_contact.*
import javax.inject.Inject

class ContactAddFragment @Inject constructor() : DaggerFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactAddToolbar.setFeatureButtonOnClickListener(View.OnClickListener {
            println("Hello World!")
        })

        contactAddToolbar.setNavigationOnClickListener {
            fragmentManager!!.popBackStack()
        }

        Glide.with(this)
                .load(R.drawable.rectangle_white)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar)
    }
}