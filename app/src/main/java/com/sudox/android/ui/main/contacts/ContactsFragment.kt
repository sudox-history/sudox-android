package com.sudox.android.ui.main.contacts

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sudox.android.R
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.database.Contact
import com.sudox.android.ui.MainActivity
import com.sudox.android.ui.adapters.ContactsAdapter
import com.sudox.android.ui.diffutil.ContactsDiffUtil
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.card_add_contact.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.include_search_navbar_addition.*
import javax.inject.Inject

class ContactsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var contactsViewModel: ContactsViewModel
    private lateinit var mainActivity: MainActivity

    private lateinit var adapter: ContactsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactsViewModel = getViewModel(viewModelFactory)
        mainActivity = activity as MainActivity
        adapter = ContactsAdapter(ArrayList(), mainActivity)

        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mainActivity.setSupportActionBar(contactsToolbar)

        initSearchAdditionalView()
        initContactsList()
        initListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_contacts, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initSearchAdditionalView() {
        searchAdditionalView.startListener = {
            val item = contactsToolbar.menu.findItem(R.id.add_contact)

            if (it) {
                item.setIcon(R.drawable.ic_close)
            } else {
                item.setIcon(R.drawable.ic_add_contact)
            }

            blackOverlayView.toggle(!it)
        }

        blackOverlayView.setOnClickListener {
            searchAdditionalView.toggle(false)
        }
    }

    @SuppressLint("ResourceType")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_contact -> {
                searchAdditionalView.toggle()
            }
        }
        return true
    }


    private fun initContactsList() {
        contactsList.adapter = adapter
        contactsList.layoutManager = LinearLayoutManager(activity)

        contactsViewModel
                .contactsLoadLiveData()
                .observe(this, Observer {
                    val result = DiffUtil.calculateDiff(ContactsDiffUtil(it, adapter.items))

                    // Update data
                    adapter.items = it
                    result.dispatchUpdatesTo(adapter)
                })
    }

    private fun setSearchContact(contact: Contact?) {
        if (contact != null) {
            val transitionSet = TransitionSet()
            transitionSet.addTransition(Fade())
            TransitionManager.beginDelayedTransition(scene_contacts_root, transitionSet)

            add_contact_hint.visibility = View.GONE
            card_add_contact.visibility = View.VISIBLE
            if (contact.firstColor != null && contact.secondColor != null) {
                val builder = StringBuilder()
                val names = contact.name.split(" ")

                if (names.isNotEmpty()) {
                    builder.append(names[0][0])
                }

                if (names.size >= 2) {
                    builder.append(names[1][0])
                }

                // Build text
                val text = builder.toString()

                // Get bitmap
                val gradientBitmap = drawGradientBitmap(contact.firstColor!!, contact.secondColor!!, text)

                // Load image
                Glide.with(this).load(gradientBitmap).into(avatar_search)
            } else {
                TODO("if photo is not gradient")
            }

            name_search.text = contact.name
        }
    }

    private fun drawGradientBitmap(firstColor: String, secondColor: String, text: String): Bitmap {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Enable antialiasing
        paint.isAntiAlias = true

        // Draw gradient
        paint.shader = LinearGradient(100F, 0F, 100F, 200F,
                Color.parseColor(firstColor), Color.parseColor(secondColor), Shader.TileMode.REPEAT)

        // Draw circle
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), 180F, paint)

        // Text bounds
        val textRect = Rect()

        // Draw text
        paint.shader = null
        paint.color = Color.WHITE
        paint.textSize = 60F
        paint.getTextBounds(text, 0, text.length, textRect)
        canvas.drawText(text, canvas.width / 2 - textRect.exactCenterX(), canvas.height / 2 - textRect.exactCenterY(), paint)

        return bitmap
    }


    private fun initListeners() {
        val nicknameRegex = ".+#.*".toRegex()

        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.matches(nicknameRegex)) {
                    nicknameEditText.inputType = InputType.TYPE_CLASS_NUMBER
                } else {
                    nicknameEditText.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
        })

        nicknameEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                contactsViewModel.contactsSearchUserByNickname(nicknameEditText.text.toString())
                        .observe(this, Observer(::setSearchContact))
                return@OnEditorActionListener true
            }
            false
        })
    }


}