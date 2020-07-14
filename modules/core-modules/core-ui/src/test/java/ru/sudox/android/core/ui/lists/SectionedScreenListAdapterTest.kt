package ru.sudox.android.core.ui.lists

import android.app.Activity
import android.os.Looper.getMainLooper
import android.view.Menu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import ru.sudox.android.core.ui.CommonUiRunner
import ru.sudox.android.core.ui.lists.dependencies.SectionedScreenListItemViewObject
import ru.sudox.android.core.ui.lists.dependencies.SectionedScreenListTestAdapter
import ru.sudox.android.core.ui.lists.model.SECTION_TYPE_CHANGED_FLAG
import ru.sudox.android.core.ui.lists.model.SectionVO
import ru.sudox.android.core.ui.lists.shadows.MENU_INFLATER_SHADOW_CALLBACK
import ru.sudox.android.core.ui.lists.shadows.MenuInflaterShadow
import ru.sudox.simplelists.loadable.LOADER_VIEW_TYPE
import ru.sudox.simplelists.model.BasicListItem

@RunWith(CommonUiRunner::class)
class SectionedScreenListAdapterTest {

    @Test
    @Config(shadows = [MenuInflaterShadow::class])
    fun testSectionReloadingWhenEnabledLoadingAndDataNotReturned() {
        MENU_INFLATER_SHADOW_CALLBACK = {
            it.add(0, 2, 0, "First")
            it.add(0, 3, 1, "Second")
        }

        val adapter = getAdapter()
        var loadingDisabledCallbackInvokingCount = 0

        adapter.forceLoadingDisabledCallback = { loadingDisabledCallbackInvokingCount++ }
        adapter.loadSectionItems = { null }

        val sectionVO = SectionVO(0, typesMenuRes = 1, defaultTypeId = 2)
        val firstItem = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))

        adapter.addSection(0, sectionVO)
        adapter.addItemsToSection(0, 0, listOf(firstItem))
        adapter.toggleLoading(0, true)
        clearInvocations(adapter)

        adapter.reloadSection(sectionVO, { it.selectedTypeId = 3 }, true, true, listOf(SECTION_TYPE_CHANGED_FLAG))
        verify(adapter).notifyItemChanged(0, listOf(SECTION_TYPE_CHANGED_FLAG))
        assertFalse(adapter.sections[0]!!.isLoading)
        assertEquals(3, sectionVO.selectedTypeId)
        assertEquals(sectionVO, adapter.currentItems[0].viewObject)
        assertEquals(1, adapter.currentItems.size)
        assertEquals(1, loadingDisabledCallbackInvokingCount)
    }

    @Test
    @Config(shadows = [MenuInflaterShadow::class])
    fun testSectionReloadingWhenEnabledLoadingAndDataReturned() {
        MENU_INFLATER_SHADOW_CALLBACK = {
            it.add(0, 2, 0, "First")
            it.add(0, 3, 1, "Second")
        }

        val adapter = getAdapter()
        var loadingDisabledCallbackInvokingCount = 0
        val firstItemOfNextSection = BasicListItem(0, SectionedScreenListItemViewObject("1 1"))
        val firstItem = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))

        adapter.forceLoadingDisabledCallback = { loadingDisabledCallbackInvokingCount++ }
        adapter.loadSectionItems = {
            if (it.selectedTypeId == 3) {
                listOf(firstItemOfNextSection)
            } else {
                listOf(firstItem)
            }
        }

        val sectionVO = SectionVO(0, typesMenuRes = 1, defaultTypeId = 2)

        adapter.addSection(0, sectionVO)
        adapter.toggleLoading(0, true)
        clearInvocations(adapter)

        shadowOf(getMainLooper()).pause()
        adapter.reloadSection(sectionVO, { it.selectedTypeId = 3 }, true, true, listOf(SECTION_TYPE_CHANGED_FLAG))
        shadowOf(getMainLooper()).idle()

        assertFalse(adapter.sections[0]!!.isLoading)
        assertEquals(3, sectionVO.selectedTypeId)
        assertEquals(sectionVO, adapter.currentItems[0].viewObject)
        assertEquals(firstItemOfNextSection, adapter.currentItems[1])
        assertEquals(1, adapter.getSectionItemsCount(0))
        assertEquals(2, adapter.currentItems.size)
    }

    @Test
    fun testSectionWithHeaderAdding() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val secondSectionVO = SectionVO(android.R.string.copy)

        adapter.addSection(0, firstSectionVO)
        adapter.addSection(1, secondSectionVO)

        assertTrue(adapter.sectionsWithHeaders.contains(0))
        assertTrue(adapter.sectionsWithHeaders.contains(1))
        assertEquals(firstSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(secondSectionVO, adapter.currentItems[1].viewObject)
        assertEquals(0, adapter.getSectionItemsCount(0))
        assertEquals(0, adapter.getSectionItemsCount(1))
        assertEquals(0, firstSectionVO.order)
        assertEquals(1, secondSectionVO.order)
    }

    @Test
    fun testSectionWithHeaderAddingWithItems() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val firstOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))

        adapter.loadSectionItems = { listOf(firstOfFirstSection) }
        adapter.addSection(0, firstSectionVO)

        assertEquals(firstSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(firstOfFirstSection, adapter.currentItems[1])
        assertEquals(1, adapter.getSectionItemsCount(0))
    }

    @Test
    @Config(shadows = [MenuInflaterShadow::class])
    fun checkThatMenuCachedDuringHeaderAdding() {
        val adapter = getAdapter()
        val sectionVO = SectionVO(android.R.string.autofill, typesMenuRes = 1, defaultTypeId = 2)
        var inflatedMenu: Menu? = null

        MENU_INFLATER_SHADOW_CALLBACK = {
            it.add(0, 2, 0, "First")
            it.add(0, 3, 1, "Second")
            inflatedMenu = it
        }

        adapter.addSection(0, sectionVO)
        assertEquals(inflatedMenu, adapter.menuCache[1])
    }

    @Test
    @Config(shadows = [MenuInflaterShadow::class])
    fun checkThatMenuRemovedFromCacheAfterHeaderRemoving() {
        val adapter = getAdapter()
        val sectionVO = SectionVO(android.R.string.autofill, typesMenuRes = 1, defaultTypeId = 2)

        MENU_INFLATER_SHADOW_CALLBACK = {
            it.add(0, 2, 0, "First")
            it.add(0, 3, 1, "Second")
        }

        adapter.addSection(0, sectionVO)
        adapter.removeSection(0)
        assertEquals(0, adapter.menuCache.size)
    }

    @Test
    fun testSectionWithHeaderRemoving() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val secondSectionVO = SectionVO(android.R.string.copy)

        adapter.addSection(0, firstSectionVO)
        adapter.addSection(1, secondSectionVO)
        adapter.removeSection(1)

        assertTrue(adapter.sectionsWithHeaders.contains(0))
        assertFalse(adapter.sectionsWithHeaders.contains(1))
        assertEquals(firstSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(1, adapter.currentItems.size)

        adapter.removeSection(0)
        assertFalse(adapter.sectionsWithHeaders.contains(0))
        assertFalse(adapter.sectionsWithHeaders.contains(1))
        assertEquals(0, adapter.currentItems.size)
    }

    @Test
    fun testItemsAddingToSectionWithHeader() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val secondSectionVO = SectionVO(android.R.string.copy)
        val firstOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))
        val secondOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 2"))
        val firstOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 1"))
        val secondOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 2"))

        adapter.addSection(0, firstSectionVO)
        adapter.addSection(1, secondSectionVO)
        adapter.addItemsToSection(0, 0, listOf(firstOfFirstSection, secondOfFirstSection))
        adapter.addItemsToSection(1, 0, listOf(firstOfSecondSection, secondOfSecondSection))

        assertEquals(firstSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(firstOfFirstSection, adapter.currentItems[1])
        assertEquals(secondOfFirstSection, adapter.currentItems[2])
        assertEquals(2, adapter.getSectionItemsCount(0))

        assertEquals(secondSectionVO, adapter.currentItems[3].viewObject)
        assertEquals(firstOfSecondSection, adapter.currentItems[4])
        assertEquals(secondOfSecondSection, adapter.currentItems[5])
        assertEquals(2, adapter.getSectionItemsCount(1))
    }

    @Test
    fun testItemsAddingToSectionWithHeaderWhenBeforeItExistsSectionWithoutHeader() {
        val adapter = getAdapter()
        val secondSectionVO = SectionVO(android.R.string.autofill)
        val firstOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))
        val secondOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 2"))
        val firstOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 1"))
        val secondOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 2"))

        adapter.addSection(0)
        adapter.addSection(1, secondSectionVO)
        adapter.addItemsToSection(0, 0, listOf(firstOfFirstSection, secondOfFirstSection))
        adapter.addItemsToSection(1, 0, listOf(firstOfSecondSection, secondOfSecondSection))

        assertEquals(firstOfFirstSection, adapter.currentItems[0])
        assertEquals(secondOfFirstSection, adapter.currentItems[1])
        assertEquals(2, adapter.getSectionItemsCount(0))

        assertEquals(secondSectionVO, adapter.currentItems[2].viewObject)
        assertEquals(firstOfSecondSection, adapter.currentItems[3])
        assertEquals(secondOfSecondSection, adapter.currentItems[4])
        assertEquals(2, adapter.getSectionItemsCount(1))
    }

    @Test
    fun testItemsRemovingFromSectionWithHeader() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val secondSectionVO = SectionVO(android.R.string.copy)
        val firstOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))
        val secondOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 2"))
        val firstOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 1"))
        val secondOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 2"))

        adapter.addSection(0, firstSectionVO)
        adapter.addSection(1, secondSectionVO)
        adapter.addItemsToSection(0, 0, listOf(firstOfFirstSection, secondOfFirstSection))
        adapter.addItemsToSection(1, 0, listOf(firstOfSecondSection, secondOfSecondSection))

        adapter.removeItemsFromSection(1, 0, 2)
        assertEquals(firstSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(firstOfFirstSection, adapter.currentItems[1])
        assertEquals(secondOfFirstSection, adapter.currentItems[2])
        assertEquals(secondSectionVO, adapter.currentItems[3].viewObject)
        assertEquals(2, adapter.getSectionItemsCount(0))
        assertEquals(0, adapter.getSectionItemsCount(1))

        adapter.removeItemsFromSection(0, 0, 2)
        assertEquals(firstSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(secondSectionVO, adapter.currentItems[1].viewObject)
        assertEquals(0, adapter.getSectionItemsCount(0))
        assertEquals(0, adapter.getSectionItemsCount(1))
    }

    @Test
    fun testItemsRemovingFromSectionWithHeaderWhenBeforeItExistsSectionWithoutHeader() {
        val adapter = getAdapter()
        val secondSectionVO = SectionVO(android.R.string.autofill)
        val firstOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))
        val secondOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 2"))
        val firstOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 1"))
        val secondOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 2"))

        adapter.addSection(0)
        adapter.addSection(1, secondSectionVO)
        adapter.addItemsToSection(0, 0, listOf(firstOfFirstSection, secondOfFirstSection))
        adapter.addItemsToSection(1, 0, listOf(firstOfSecondSection, secondOfSecondSection))

        adapter.removeItemsFromSection(1, 0, 2)
        assertEquals(firstOfFirstSection, adapter.currentItems[0])
        assertEquals(secondOfFirstSection, adapter.currentItems[1])
        assertEquals(secondSectionVO, adapter.currentItems[2].viewObject)
        assertEquals(2, adapter.getSectionItemsCount(0))
        assertEquals(0, adapter.getSectionItemsCount(1))

        adapter.removeItemsFromSection(0, 0, 2)
        assertEquals(secondSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(0, adapter.getSectionItemsCount(0))
        assertEquals(0, adapter.getSectionItemsCount(1))
    }

    @Test
    fun testItemsGettingFromSectionWithHeader() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val secondSectionVO = SectionVO(android.R.string.copy)
        val firstOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))
        val secondOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 2"))
        val firstOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 1"))
        val secondOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 2"))

        adapter.addSection(0, firstSectionVO)
        adapter.addSection(1, secondSectionVO)
        adapter.addItemsToSection(0, 0, listOf(firstOfFirstSection, secondOfFirstSection))
        adapter.addItemsToSection(1, 0, listOf(firstOfSecondSection, secondOfSecondSection))

        val firstItems = adapter.getItemsFromSection(0, 0, 2, false)
        assertEquals(firstOfFirstSection, firstItems[0])
        assertEquals(secondOfFirstSection, firstItems[1])
        assertEquals(2, firstItems.size)

        val secondItems = adapter.getItemsFromSection(1, 0, 2, false)
        assertEquals(firstOfSecondSection, secondItems[0])
        assertEquals(secondOfSecondSection, secondItems[1])
        assertEquals(2, secondItems.size)
    }

    @Test
    fun testItemsGettingFromSectionWithHeaderWhenBeforeItExistsSectionWithoutHeader() {
        val adapter = getAdapter()
        val secondSectionVO = SectionVO(android.R.string.autofill)
        val firstOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))
        val secondOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 2"))
        val firstOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 1"))
        val secondOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 2"))

        adapter.addSection(0)
        adapter.addSection(1, secondSectionVO)
        adapter.addItemsToSection(0, 0, listOf(firstOfFirstSection, secondOfFirstSection))
        adapter.addItemsToSection(1, 0, listOf(firstOfSecondSection, secondOfSecondSection))

        val firstItems = adapter.getItemsFromSection(0, 0, 2, false)
        assertEquals(firstOfFirstSection, firstItems[0])
        assertEquals(secondOfFirstSection, firstItems[1])
        assertEquals(2, firstItems.size)

        val secondItems = adapter.getItemsFromSection(1, 0, 2, false)
        assertEquals(firstOfSecondSection, secondItems[0])
        assertEquals(secondOfSecondSection, secondItems[1])
        assertEquals(2, secondItems.size)
    }

    @Test
    fun testItemsUpdatingInSectionWithHeader() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val firstOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 1")).apply { viewAnimationState = "Animation" }

        adapter.addSection(0, firstSectionVO)
        adapter.addItemsToSection(0, 0, listOf(firstOfFirstSection))

        val firstOfFirstSectionUpdated = BasicListItem(0, SectionedScreenListItemViewObject("0 1 Updated"))

        adapter.updateSectionItem(0, 0, firstOfFirstSectionUpdated)
        assertEquals(firstOfFirstSectionUpdated, adapter.currentItems[1])
        assertEquals("Animation", adapter.currentItems[1].viewAnimationState)
    }

    @Test
    fun testItemsUpdatingInSectionWithHeaderWhenBeforeItExistsSectionWithoutHeader() {
        val adapter = getAdapter()
        val secondSectionVO = SectionVO(android.R.string.autofill)
        val firstOfFirstSection = BasicListItem(0, SectionedScreenListItemViewObject("0 1"))
        val firstOfSecondSection = BasicListItem(0, SectionedScreenListItemViewObject("1 1"))

        adapter.addSection(0)
        adapter.addSection(1, secondSectionVO)
        adapter.addItemsToSection(0, 0, listOf(firstOfFirstSection))
        adapter.addItemsToSection(1, 0, listOf(firstOfSecondSection))

        val firstOfFirstSectionUpdated = BasicListItem(0, SectionedScreenListItemViewObject("0 1 Updated"))
        val firstOfSecondSectionUpdated = BasicListItem(0, SectionedScreenListItemViewObject("1 1 Updated"))

        adapter.updateSectionItem(0, 0, firstOfFirstSectionUpdated)
        adapter.updateSectionItem(1, 0, firstOfSecondSectionUpdated)
        assertEquals(firstOfFirstSectionUpdated, adapter.currentItems[0])
        assertEquals(firstOfSecondSectionUpdated, adapter.currentItems[2])
    }

    @Test
    fun testItemsChangingInSectionWithHeader() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val first = BasicListItem(0, SectionedScreenListItemViewObject("First")).apply { viewAnimationState = "1" }
        val second = BasicListItem(0, SectionedScreenListItemViewObject("Second")).apply { viewAnimationState = "2" }
        val third = BasicListItem(0, SectionedScreenListItemViewObject("Third")).apply { viewAnimationState = "3" }

        adapter.addSection(0, firstSectionVO)
        adapter.addItemsToSection(0, 0, listOf(first, second, third))

        val firstUpdated = BasicListItem(0, SectionedScreenListItemViewObject("First Updated", requestedId = "First"))
        val secondUpdated = BasicListItem(0, SectionedScreenListItemViewObject("Second Updated", requestedId = "Second"))
        val thirdUpdated = BasicListItem(0, SectionedScreenListItemViewObject("Third Updated", requestedId = "Third"))

        adapter.changeSectionItems(0, listOf(firstUpdated, secondUpdated, thirdUpdated), false)
        assertEquals(firstSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(firstUpdated, adapter.currentItems[1])
        assertEquals(secondUpdated, adapter.currentItems[2])
        assertEquals(thirdUpdated, adapter.currentItems[3])
        assertEquals("1", adapter.currentItems[1].viewAnimationState)
        assertEquals("2", adapter.currentItems[2].viewAnimationState)
        assertEquals("3", adapter.currentItems[3].viewAnimationState)
        assertEquals(3, adapter.getSectionItemsCount(0))
    }

    @Test
    fun testItemsChangingInSectionWithHeaderWhenEnabledLoading() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val first = BasicListItem(0, SectionedScreenListItemViewObject("First")).apply { viewAnimationState = "1" }
        val second = BasicListItem(0, SectionedScreenListItemViewObject("Second")).apply { viewAnimationState = "2" }
        val third = BasicListItem(0, SectionedScreenListItemViewObject("Third")).apply { viewAnimationState = "3" }

        adapter.addSection(0, firstSectionVO)
        adapter.addItemsToSection(0, 0, listOf(first, second, third))
        adapter.toggleLoading(0, true)

        val firstUpdated = BasicListItem(0, SectionedScreenListItemViewObject("First Updated", requestedId = "First"))
        val secondUpdated = BasicListItem(0, SectionedScreenListItemViewObject("Second Updated", requestedId = "Second"))
        val thirdUpdated = BasicListItem(0, SectionedScreenListItemViewObject("Third Updated", requestedId = "Third"))

        adapter.changeSectionItems(0, listOf(firstUpdated, secondUpdated, thirdUpdated), false)
        assertEquals(firstSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(firstUpdated, adapter.currentItems[1])
        assertEquals(secondUpdated, adapter.currentItems[2])
        assertEquals(thirdUpdated, adapter.currentItems[3])
        assertEquals(LOADER_VIEW_TYPE, adapter.currentItems[4].viewType)
        assertEquals("1", adapter.currentItems[1].viewAnimationState)
        assertEquals("2", adapter.currentItems[2].viewAnimationState)
        assertEquals("3", adapter.currentItems[3].viewAnimationState)
        assertEquals(3, adapter.getSectionItemsCount(0))
    }

    @Test
    fun testLoadingOnSectionWithHeader() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)
        val first = BasicListItem(0, SectionedScreenListItemViewObject("First"))
        val second = BasicListItem(0, SectionedScreenListItemViewObject("Second"))
        val third = BasicListItem(0, SectionedScreenListItemViewObject("Third"))

        adapter.addSection(0, firstSectionVO)
        adapter.addItemsToSection(0, 0, listOf(first, second, third))
        adapter.toggleLoading(0, true)

        assertEquals(firstSectionVO, adapter.currentItems[0].viewObject)
        assertEquals(first, adapter.currentItems[1])
        assertEquals(second, adapter.currentItems[2])
        assertEquals(third, adapter.currentItems[3])
        assertEquals(LOADER_VIEW_TYPE, adapter.currentItems[4].viewType)
    }

    @Test
    fun testInitialLoadingDetecting() {
        val adapter = getAdapter()
        val firstSectionVO = SectionVO(android.R.string.autofill)

        adapter.addSection(0, firstSectionVO)
        adapter.toggleLoading(0, true)

        assertTrue(adapter.isSectionInInitialLoading(0))
    }

    private fun getAdapter(): SectionedScreenListAdapter {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val list = RecyclerView(activity)
        val adapter = Mockito.spy(SectionedScreenListTestAdapter(activity))

        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter

        activity.setContentView(list)
        controller.create()
        controller.visible()
        Mockito.clearInvocations(list.adapter)

        return adapter
    }
}