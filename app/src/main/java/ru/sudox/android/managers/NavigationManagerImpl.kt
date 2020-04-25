package ru.sudox.android.managers

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sudox.android.R
import ru.sudox.android.auth.ui.phone.AuthPhoneController
import ru.sudox.android.core.CoreController
import ru.sudox.android.core.managers.AUTH_ROOT_TAG
import ru.sudox.android.core.managers.NavigationManager
import ru.sudox.android.managers.handlers.move.LeftMoveHandler
import ru.sudox.android.managers.handlers.move.RightMoveHandler
import ru.sudox.android.dialogs.DialogsController
import ru.sudox.android.people.PeopleController
import ru.sudox.android.people.ProfileController
import java.util.Stack

private const val NAVIGATION_VIEW_CURRENT_ITEM_ID_KEY = "navigation_view_current_item_id"
private const val NAVIGATION_VIEW_VISIBILITY_KEY = "bottom_navigation_view_visibility_key"
private const val MAIN_CONTROLLERS_COUNT_KEY = "main_controllers_count"
private const val MAIN_CONTROLLER_AT_VALUE_KEY = "main_controllers_value_" // index
private const val MAIN_CONTROLLER_AT_KEY_KEY = "main_controllers_key_" // index
private const val LOADED_MAIN_CONTROLLERS_COUNT = "loaded_main_controllers_count"
private const val LOADED_MAIN_CONTROLLER_AT = "loaded_main_controller_at_" // index
private const val ANIMATION_DURATION = 140L

private const val PEOPLE_TAG = 2
private const val DIALOGS_TAG = 3
private const val BLOG_TAG = 4

class NavigationManagerImpl(
        val routerProvider: Lazy<Router>,
        val bottomNavigationView: BottomNavigationView
) : NavigationManager, BottomNavigationView.OnNavigationItemSelectedListener {

    private val bottomNavViewTags = HashMap<Int, Int>()
    private val bottomNavViewIds = HashMap<Int, Int>()

    private var loadedMainTags = HashSet<Int>()
    private var mainControllersTags = HashMap<String, Int>()
    private var blockNavigationViewCallback = false
    private var isLaunched = false

    init {
        bottomNavigationView.apply {
            addItem(this, PEOPLE_TAG, R.string.people, R.drawable.ic_group)
            addItem(this, DIALOGS_TAG, R.string.messages, R.drawable.ic_chat_bubble)
            addItem(this, BLOG_TAG, R.string.blog, R.drawable.ic_account)

            setOnNavigationItemSelectedListener(this@NavigationManagerImpl)
        }
    }

    private fun addItem(view: BottomNavigationView, tag: Int, @StringRes titleId: Int, @DrawableRes iconId: Int) {
        val id = View.generateViewId()

        view.menu.add(0, id, 0, titleId).apply {
            setIcon(iconId)
        }

        bottomNavViewTags[id] = tag
        bottomNavViewIds[tag] = id
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (blockNavigationViewCallback) {
            return true
        }

        val tag = bottomNavViewTags[item.itemId]!!
        val selectedTag = getCurrentTag()
        val router = routerProvider.value

        if (tag == selectedTag) {
            val controller = router.backstack.last().controller as CoreController

            if (!controller.isInStartState()) {
                controller.toStartState()
            }

            return true
        }

        val changeHandler = if (tag > selectedTag) {
            RightMoveHandler(ANIMATION_DURATION)
        } else {
            LeftMoveHandler(ANIMATION_DURATION)
        }

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
            }, changeHandler)
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

            router.pushController(RouterTransaction
                    .with(controller)
                    .pushChangeHandler(changeHandler))
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
            val previous = backstack.last().controller
            val previousTag = mainControllersTags[previous.instanceId]

            if (previousTag != null) {
                if (previousTag != currentTag) {
                    loadedMainTags.remove(currentTag)

                    blockNavigationViewCallback = true
                    bottomNavigationView.selectedItemId = getTagItemId(previousTag)
                    blockNavigationViewCallback = false
                }

                bottomNavigationView.visibility = View.VISIBLE
            } else {
                bottomNavigationView.visibility = View.GONE
                loadedMainTags.remove(currentTag)
            }

            val changeHandler = if (previousTag == null || currentTag == null || previousTag <= currentTag) {
                LeftMoveHandler(ANIMATION_DURATION)
            } else {
                RightMoveHandler(ANIMATION_DURATION)
            }

            router.setBackstack(backstack, changeHandler)
            return true
        }

        return false
    }

    override fun showRootChild(controller: Controller) {
        if (bottomNavigationView.visibility == View.VISIBLE) {
            mainControllersTags[controller.instanceId] = getCurrentTag()
        }

        routerProvider.value.pushController(RouterTransaction
                .with(controller)
                .pushChangeHandler(RightMoveHandler(ANIMATION_DURATION)))
    }

    override fun showRoot(tag: Int) {
        val router = routerProvider.value
        val transaction = if (tag == AUTH_ROOT_TAG) {
            val controller = AuthPhoneController()
            val transaction = RouterTransaction.with(controller)

            if (isLaunched) {
                transaction.pushChangeHandler(LeftMoveHandler(ANIMATION_DURATION))
            }

            bottomNavigationView.visibility = View.GONE
            loadedMainTags.clear()
            mainControllersTags.clear()

            transaction
        } else {
            val controller = PeopleController()
            val transaction = RouterTransaction.with(controller)

            if (isLaunched) {
                transaction.pushChangeHandler(RightMoveHandler(ANIMATION_DURATION))
            }

            loadedMainTags.add(PEOPLE_TAG)
            mainControllersTags[controller.instanceId] = PEOPLE_TAG

            blockNavigationViewCallback = true
            bottomNavigationView.visibility = View.VISIBLE
            bottomNavigationView.selectedItemId = getTagItemId(PEOPLE_TAG)
            blockNavigationViewCallback = false

            transaction
        }

        if (!isLaunched) {
            transaction.pushChangeHandler(FadeChangeHandler(ANIMATION_DURATION))
            isLaunched = true
        }

        router.setRoot(transaction)
    }

    override fun showSubRoot(controller: Controller) {
        routerProvider.value.pushController(RouterTransaction
                .with(controller)
                .pushChangeHandler(RightMoveHandler(ANIMATION_DURATION))
                .popChangeHandler(LeftMoveHandler(ANIMATION_DURATION)))

        bottomNavigationView.visibility = View.GONE
    }

    override fun restoreState(bundle: Bundle?) {
        if (bundle != null) {
            bottomNavigationView.visibility = bundle.getInt(NAVIGATION_VIEW_VISIBILITY_KEY)

            blockNavigationViewCallback = true
            bottomNavigationView.selectedItemId = getTagItemId(bundle.getInt(NAVIGATION_VIEW_CURRENT_ITEM_ID_KEY))
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
            bundle.putInt(NAVIGATION_VIEW_CURRENT_ITEM_ID_KEY, getCurrentTag())
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

    override fun isContentUsesAllLayout(): Boolean {
        return bottomNavigationView.visibility == View.GONE
    }

    private fun getCurrentTag()
            = bottomNavViewTags[bottomNavigationView.selectedItemId]!!

    private fun getTagItemId(tag: Int)
            = bottomNavViewIds[tag]!!
}