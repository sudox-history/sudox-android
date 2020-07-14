package ru.sudox.android.core.ui.lists.holder

import android.animation.ObjectAnimator
import android.graphics.drawable.RotateDrawable
import android.view.View
import com.google.android.material.button.MaterialButton
import ru.sudox.android.core.ui.R
import ru.sudox.android.core.ui.lists.model.SECTION_TYPE_CHANGED_FLAG
import ru.sudox.android.core.ui.lists.model.SectionVO
import ru.sudox.android.core.ui.popup.CustomPopupMenu
import ru.sudox.android.core.ui.popup.CustomPopupMenuBuilder
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListItem

private const val TOGGLE_TYPE_ICON_ANIMATION_DURATION = 300L
private const val COLLAPSING_ICON_ANIMATION_DURATION = 300L

/**
 * ViewHolder для заголовка секции
 *
 * @param view View, которую содержит ViewHolder.
 * @param changeType Функция для изменения типа.
 * @param changeSorting Функция для изменения сортировки.
 * @param collapseSection Функция для скрытия/показа секции.
 * @param getMenu Функция для получения меню.
 */
class SectionHeaderViewHolder(
    view: View,
    private val changeType: (SectionVO, Int) -> (Unit),
    private val changeSorting: (SectionVO, Int) -> (Unit),
    private val collapseSection: (SectionVO) -> (Unit),
    private val getMenu: (Int) -> (CustomPopupMenuBuilder)
) : BasicListHolder<SectionVO>(view) {

    private var toggleTypeIcon = RotateDrawable()
    private var toggleTypeIconAnimator = ObjectAnimator.ofInt(toggleTypeIcon, "level", 0, 5000).apply {
        duration = TOGGLE_TYPE_ICON_ANIMATION_DURATION
    }

    private var collapsingIcon = RotateDrawable()
    private var collapsingIconAnimator = ObjectAnimator.ofInt(collapsingIcon, "level", 0, 5000).apply {
        duration = COLLAPSING_ICON_ANIMATION_DURATION
    }

    private val titleTextView = view.findViewById<MaterialButton>(R.id.sectionHeaderTitle)
    private val counterButton = view.findViewById<MaterialButton>(R.id.sectionHeaderCounterButton)
    private val actionButton = view.findViewById<MaterialButton>(R.id.sectionHeaderActionButton)
    private var currentItem: BasicListItem<SectionVO>? = null

    init {
        actionButton.background = null
        counterButton.background = null
        titleTextView.background = null
    }

    init {
        titleTextView.setOnClickListener {
            openPopup(currentItem!!.viewObject!!, it, { vo ->
                vo.typesMenuRes
            }, changeType, {
                toggleTypeIconAnimator.reverse()
            })

            toggleTypeIconAnimator.start()
        }

        actionButton.setOnClickListener {
            val vo = currentItem!!.viewObject!!

            when {
                vo.sortsMenuRes != 0 -> openPopup(vo, it, { vo.sortsMenuRes }, changeSorting)
                vo.isCollapsed -> {
                    collapsingIconAnimator.reverse()
                    collapseSection(vo)
                }
                else -> {
                    collapsingIconAnimator.start()
                    collapseSection(vo)
                }
            }
        }

        toggleTypeIcon.drawable = view.context.getDrawable(R.drawable.ic_baseline_keyboard_arrow_up)
        collapsingIcon.drawable = view.context.getDrawable(R.drawable.ic_baseline_keyboard_arrow_down)
    }

    override fun bind(item: BasicListItem<SectionVO>, changePayload: List<Any>?) {
        val vo = item.viewObject!!

        if (changePayload == null) {
            titleTextView.let {
                if (vo.typesMenuRes != 0) {
                    it.icon = toggleTypeIcon
                    it.text = getSelectedItemTitle(vo)
                    it.isClickable = true
                } else {
                    it.setText(vo.titleRes)
                    it.isClickable = false
                    it.icon = null
                }
            }

            when {
                vo.sortsMenuRes != 0 -> {
                    actionButton.setIconResource(R.drawable.ic_baseline_filter_list)
                    actionButton.visibility = View.VISIBLE
                    counterButton.visibility = View.GONE
                }
                vo.canCollapse -> {
                    collapsingIcon.level = if (vo.isCollapsed) {
                        5000
                    } else {
                        0
                    }

                    actionButton.icon = collapsingIcon
                    actionButton.visibility = View.VISIBLE
                    counterButton.visibility = View.GONE
                }
                vo.countValue > 0 -> {
                    actionButton.icon = null
                    actionButton.visibility = View.GONE
                    counterButton.visibility = View.VISIBLE
                    counterButton.text = vo.countValue.toString()
                }
                else -> {
                    actionButton.icon = null
                    actionButton.visibility = View.GONE
                    counterButton.visibility = View.GONE
                    counterButton.text = null
                }
            }

            currentItem = item
        } else {
            changePayload.forEach {
                if (it == SECTION_TYPE_CHANGED_FLAG) {
                    titleTextView.text = getSelectedItemTitle(vo)
                }
            }
        }
    }

    override fun cancelAnimations() {
        if (toggleTypeIconAnimator.isRunning) {
            toggleTypeIconAnimator.cancel()
        }

        if (collapsingIconAnimator.isRunning) {
            collapsingIconAnimator.cancel()
        }
    }

    private inline fun openPopup(
        vo: SectionVO,
        anchor: View,
        crossinline getMenuId: (SectionVO) -> (Int),
        crossinline clickCallback: (SectionVO, Int) -> (Unit),
        noinline dismissListener: ((() -> (Unit)))? = null
    ) {
        val menu = CustomPopupMenu(anchor.context)

        if (dismissListener != null) {
            menu.setOnDismissListener(dismissListener)
        }

        menu.anchorView = anchor
        menu.clickCallback = { item -> clickCallback(vo, item.itemId) }
        menu.menu = getMenu(getMenuId(vo))
        menu.show()
    }

    private fun getSelectedItemTitle(vo: SectionVO) = getMenu(vo.typesMenuRes).selectedItem!!.title
}