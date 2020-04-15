package ru.sudox.android.managers

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sudox.android.R
import ru.sudox.android.auth.ui.phone.AuthPhoneController
import ru.sudox.android.core.managers.AUTH_ROOT_TAG
import ru.sudox.android.core.managers.MAIN_ROOT_TAG
import ru.sudox.android.core.managers.NewNavigationManager
import ru.sudox.android.messages.DialogsController
import ru.sudox.android.people.PeopleController
import ru.sudox.android.people.ProfileController
import ru.sudox.design.bottomnavigationview.addItem
import java.util.Stack

private const val NAVIGATION_VIEW_CURRENT_ITEM_ID_KEY = "navigation_view_current_item_id"
private const val NAVIGATION_VIEW_VISIBILITY_KEY = "bottom_navigation_view_visibility_key"
private const val MAIN_CONTROLLERS_COUNT_KEY = "main_controllers_count"
private const val MAIN_CONTROLLER_AT_VALUE_KEY = "main_controllers_value_" // index
private const val MAIN_CONTROLLER_AT_KEY_KEY = "main_controllers_key_" // index
private const val LOADED_MAIN_CONTROLLERS_COUNT = "loaded_main_controllers_count"
private const val LOADED_MAIN_CONTROLLER_AT = "loaded_main_controller_at_" // index

private const val PEOPLE_TAG = 2
private const val DIALOGS_TAG = 3
private const val PROFILE_TAG = 4

class AppNavigationManager(
        val routerProvider: Lazy<Router>,
        val bottomNavigationView: BottomNavigationView
) : NewNavigationManager, BottomNavigationView.OnNavigationItemSelectedListener {

    private var loadedMainTags = HashSet<Int>()
    private var mainControllersTags = HashMap<String, Int>()
    private var blockNavigationViewCallback = false

    init {
        bottomNavigationView.apply {
            addItem(PEOPLE_TAG, R.string.people, R.drawable.ic_group)
            addItem(DIALOGS_TAG, R.string.messages, R.drawable.ic_chat_bubble)
            addItem(PROFILE_TAG, R.string.profile, R.drawable.ic_account)

            setOnNavigationItemSelectedListener(this@AppNavigationManager)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (blockNavigationViewCallback) {
            return true
        }

        val tag = item.itemId
        val router = routerProvider.value

        if (loadedMainTags.contains(tag)) {
            val backstack = router.backstack
            val iterator = backstack.iterator()
            val stack = Stack<RouterTransaction>()

            while (iterator.hasNext()) {
                val next = iterator.next()

                if (mainControllersTags[next.controller.instanceId] == tag) {
                    iterator.remove()
                    stack.push(next)
                }
            }

            router.setBackstack(backstack.apply {
                addAll(stack)
            }, null)
        } else {
            val controller = if (tag == PEOPLE_TAG) {
                PeopleController()
            } else if (tag == DIALOGS_TAG) {
                DialogsController()
            } else {
                ProfileController()
            }

            mainControllersTags[controller.instanceId] = tag
            loadedMainTags.add(tag)

            router.pushController(RouterTransaction.with(controller))
        }

        return true
    }

    @ExperimentalStdlibApi
    override fun popBackstack(): Boolean {
        val router = routerProvider.value
        val backstack = router.backstack
        val currentId = backstack.removeLast().controller.instanceId
        val currentTag = mainControllersTags.remove(currentId)

        if (backstack.isNotEmpty()) {
            router.setBackstack(backstack, null)

            val previous = backstack.last().controller
            val previousTag = mainControllersTags[previous.instanceId]

            if (previousTag != null) {
                if (previousTag != currentTag) {
                    loadedMainTags.remove(currentTag)

                    blockNavigationViewCallback = true
                    bottomNavigationView.selectedItemId = previousTag
                    blockNavigationViewCallback = false
                }

                bottomNavigationView.visibility = View.VISIBLE
            } else {
                bottomNavigationView.visibility = View.GONE
                loadedMainTags.remove(currentTag)
            }

            return true
        }

        return false
    }

    override fun showRootChild(controller: Controller) {
        if (bottomNavigationView.visibility == View.VISIBLE) {
            mainControllersTags[controller.instanceId] = bottomNavigationView.selectedItemId
        }

        routerProvider.value.pushController(RouterTransaction.with(controller))
    }

    override fun showRoot(tag: Int) {
        if (tag == AUTH_ROOT_TAG) {
            val controller = AuthPhoneController()
            val transaction = RouterTransaction.with(controller)

            bottomNavigationView.visibility = View.GONE
            loadedMainTags.clear()
            mainControllersTags.clear()

            routerProvider.value.setRoot(transaction)
        } else if (tag == MAIN_ROOT_TAG) {
            bottomNavigationView.apply {
                visibility = View.VISIBLE
                selectedItemId = PEOPLE_TAG
            }
        }
    }

    override fun showSubRoot(controller: Controller) {
        // TODO: Переключение на сообщения
    }

    override fun restoreState(bundle: Bundle?) {
        if (bundle != null) {
            bottomNavigationView.visibility = bundle.getInt(NAVIGATION_VIEW_VISIBILITY_KEY)

            blockNavigationViewCallback = true
            bottomNavigationView.selectedItemId = bundle.getInt(NAVIGATION_VIEW_CURRENT_ITEM_ID_KEY)
            blockNavigationViewCallback = false

            repeat(bundle.getInt(LOADED_MAIN_CONTROLLERS_COUNT)) {
                loadedMainTags.add(bundle.getInt("$LOADED_MAIN_CONTROLLER_AT$it"))
            }

            repeat(bundle.getInt(MAIN_CONTROLLERS_COUNT_KEY)) {
                val key = bundle.getString("$MAIN_CONTROLLER_AT_KEY_KEY$it")!!
                val value = bundle.getInt("$MAIN_CONTROLLER_AT_VALUE_KEY$it")

                mainControllersTags[key] = value
            }
        }
    }

    override fun saveState(bundle: Bundle?) {
        if (bundle != null) {
            bundle.putInt(NAVIGATION_VIEW_CURRENT_ITEM_ID_KEY, bottomNavigationView.selectedItemId)
            bundle.putInt(NAVIGATION_VIEW_VISIBILITY_KEY, bottomNavigationView.visibility)
            bundle.putInt(LOADED_MAIN_CONTROLLERS_COUNT, loadedMainTags.size)
            bundle.putInt(MAIN_CONTROLLERS_COUNT_KEY, mainControllersTags.size)

            loadedMainTags.forEachIndexed { index, tag ->
                bundle.putInt("$LOADED_MAIN_CONTROLLER_AT$index", tag)
            }

            mainControllersTags.entries.forEachIndexed { index, entry ->
                bundle.putString("$MAIN_CONTROLLER_AT_KEY_KEY$index", entry.key)
                bundle.putInt("$MAIN_CONTROLLER_AT_VALUE_KEY$index", entry.value)
            }
        }
    }
}