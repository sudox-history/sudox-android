package com.sudox.messenger.android.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.news.vos.NewsVO
import kotlinx.android.synthetic.main.fragment_profile.newsItemView

class ProfileFragment : CoreFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        screenManager!!.reset()

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsItemView.vo = NewsVO(1L, "Maxim Mityushkin", 1L, "Ура! Посты работают!", System.currentTimeMillis() - 10000L)
    }
}