package com.sudox.messenger.android.people

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.appbar.AppBar
import com.sudox.design.appbar.AppBarVO
import com.sudox.messenger.android.core.CoreFragment
import kotlinx.android.synthetic.main.fragment_profile.appbarLayout
import kotlinx.android.synthetic.main.fragment_profile.nestedScroll
import kotlinx.android.synthetic.main.fragment_profile.recyclerView

class ProfileFragment : CoreFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        screenManager!!.reset()

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var toolbarScrolled = 0

        nestedScroll.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            var height = 0

            for (i in 0 until appbarLayout.childCount - 1) {
                height += appbarLayout.getChildAt(i).measuredHeight
            }

            if (toolbarScrolled <= height) {
                toolbarScrolled += scrollY - oldScrollY

                if (toolbarScrolled > height) {
                    toolbarScrolled = height
                }

                if (toolbarScrolled < 0) {
                    toolbarScrolled = 0
                }
            }

            appbarLayout.translationY = (scrollY - toolbarScrolled).toFloat()
            nestedScroll.forceLayout()
        }

        recyclerView.translationZ = -1F
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = object : RecyclerView.Adapter<TestViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
                return TestViewHolder(AppCompatTextView(context))
            }

            override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
                holder.view.text = "Sample text: ${position + 1}"
            }

            override fun getItemCount(): Int {
                return 500
            }
        }

        appbarLayout.appBar = AppBar(context!!)
        appbarLayout.addView(RecyclerView(context!!).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = object : RecyclerView.Adapter<TestViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
                    return TestViewHolder(AppCompatTextView(context))
                }

                override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
                    holder.view.text = "Sample text at header: ${position + 1}"
                }

                override fun getItemCount(): Int {
                    return 25
                }
            }
        })

        appbarLayout.addView(TextView(context).apply {
            text = "Представь типо это табы"
        })

        appbarLayout.appBar!!.vo = object : AppBarVO {
            override fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>? {
                return null
            }

            override fun getButtonsAtRight(): Array<Triple<Int, Int, Int>>? {
                return null
            }

            override fun getViewAtLeft(context: Context): View? {
                return null
            }

            override fun getViewAtRight(context: Context): View? {
                return null
            }

            override fun getTitle(): Int {
                return R.string.activity
            }
        }
    }

    inner class TestViewHolder(val view: AppCompatTextView) : RecyclerView.ViewHolder(view)
}
