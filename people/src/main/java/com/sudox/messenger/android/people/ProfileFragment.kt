package com.sudox.messenger.android.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.countries.COUNTRIES
import com.sudox.messenger.android.countries.views.PhoneEditText
import kotlinx.android.synthetic.main.fragment_profile.editTextLayout

class ProfileFragment : CoreFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextLayout.childView = PhoneEditText(context!!).apply {
            autofillMyNumber = true
            id = View.generateViewId()
            vo = COUNTRIES["UA"]
        }

        editTextLayout.postDelayed({
            editTextLayout.errorText = "Error"
        }, 6000L)
    }

    inner class TestViewHolder(val view: AppCompatTextView) : RecyclerView.ViewHolder(view)
}
