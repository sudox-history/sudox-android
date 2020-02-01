package com.sudox.messenger.android.messages

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerFragment
import kotlinx.android.synthetic.main.fragment_messages.*

class MessagesFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        testDialogItem1.setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus enim ligula")
        testDialogItem1.setIsNewMessage(false)
        testDialogItem1.setCountMessages(100)
        testDialogItem1.setLastDate("21 дек.")
        testDialogItem1.setDialogName("Антон")
        testDialogItem1.setDialogImage(getDrawable(context!!, R.drawable.drawable_photo_1)!!)


        testDialogItem2.setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus enim ligula")
        testDialogItem2.setIsNewMessage(true)
        testDialogItem2.setCountMessages(202)
        testDialogItem2.setLastDate("пн")
        testDialogItem2.setDialogName("ЯроСЛАУ")
        testDialogItem2.setLastMessageByUser(true, true)
        testDialogItem2.setDialogImage(getDrawable(context!!, R.drawable.drawable_photo_2)!!)

    }

    override fun getPageTitle(context: Context): CharSequence? {
        return context.getString(R.string.messages)
    }

    override fun onPageSelected(activity: CoreActivity) {
        activity.getApplicationBarManager().let {
            it.reset(false)
            it.setListener(this)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun onButtonClicked(tag: Int) {

    }

}