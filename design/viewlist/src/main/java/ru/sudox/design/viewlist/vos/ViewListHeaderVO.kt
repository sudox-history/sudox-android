package ru.sudox.design.viewlist.vos

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat.readBoolean
import androidx.core.os.ParcelCompat.writeBoolean
import ru.sudox.design.popup.vos.PopupItemVO

/**
 * ViewObject для шапки.
 */
abstract class ViewListHeaderVO() : Parcelable {

    open var type: Int = 0
    open var isItemsHidden: Boolean = false
    open var isContentLoading: Boolean = false
    open var isInClearLoading: Boolean = false
    open var selectedToggleTag: Int = 0
    open var selectedFunctionButtonToggleTags: IntArray? = null
    open var nestedRecyclerViewParcelable: Parcelable? = null
    open var cachedPosition = -1

    @Suppress("unused")
    constructor(source: Parcel) : this() {
        source.let {
            isItemsHidden = readBoolean(source)
            isContentLoading = readBoolean(source)
            isInClearLoading = readBoolean(source)
            selectedToggleTag = it.readInt()
            selectedFunctionButtonToggleTags = it.createIntArray()
            nestedRecyclerViewParcelable = it.readParcelable(javaClass.classLoader)
            cachedPosition = it.readInt()
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.let {
            it.writeString(javaClass.name)
            writeBoolean(it, isItemsHidden)
            writeBoolean(it, isContentLoading)
            writeBoolean(it, isInClearLoading)
            it.writeInt(selectedToggleTag)
            it.writeIntArray(selectedFunctionButtonToggleTags)
            it.writeParcelable(nestedRecyclerViewParcelable, 0)
            it.writeInt(cachedPosition)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * Проверяет возможность отображения элемента загрузчика
     */
    fun canShowLoader(): Boolean {
        return !isContentLoading && (!canHideItems() || !isItemsHidden)
    }

    /**
     * Проверяет возможность скрытия элемента загрузчика
     */
    fun canHideLoader(): Boolean {
        return isContentLoading && (!canHideItems() || !isItemsHidden)
    }

    /**
     * Проверяет отображение загрузчика
     */
    fun isLoaderShowing(): Boolean {
        return isContentLoading && ((canHideItems() && !isItemsHidden) || (!canHideItems()))
    }

    /**
     * Выбирает опцию функциональной кнопки и сопоставляет её опциональной кнопки переключателя
     *
     * @param functionalToggleTag Тег опции функциональной кнопки
     */
    fun selectFunctionalToggleTag(functionalToggleTag: Int) {
        selectedFunctionButtonToggleTags!![selectedToggleTag] = functionalToggleTag
    }

    /**
     * Возвращает опцию функциональной кнопки, сопоставленную опции кнопки переключателя
     *
     * @param toggleTag Опция кнопки переключателя (по-умолчанию выбирается выбранная)
     * @return Опция функциональной кнопки
     */
    fun getSelectedFunctionalToggleTag(toggleTag: Int = selectedToggleTag): Int {
        return selectedFunctionButtonToggleTags!![toggleTag]
    }

    /**
     * Возвращает опции переключателя
     *
     * @param context Контекст приложения/активности
     * @return ViewObject'ы элементов Popup-окна
     */
    abstract fun getToggleOptions(context: Context): List<PopupItemVO<*>>

    /**
     * Возвращает ID иконки функциональной кнопки
     * Если не нужна, то нужно вернуть 0
     *
     * @return ID иконки функциональной кнопки
     */
    abstract fun getFunctionButtonIconId(): Int

    /**
     * Возвращает опции функциональной кнопки (если они есть)
     *
     * @param context Контекст приложения/активности
     * @return ViewObject'ы элементов Popup-окна
     */
    abstract fun getFunctionButtonToggleOptions(context: Context): List<PopupItemVO<*>>?

    /**
     * Определяет возможность сортировки элементов после шапки
     *
     * @return Можно ли сортировать предметы после себя?
     */
    abstract fun canSortItems(): Boolean

    /**
     * Определяет возможность скрытия элементов после шапки
     *
     * @return Можно ли скрыть предметы после себя?
     */
    abstract fun canHideItems(): Boolean

    companion object CREATOR : Parcelable.Creator<ViewListHeaderVO> {
        override fun createFromParcel(source: Parcel): ViewListHeaderVO {
            val creator = Class.forName(source.readString()!!)
                    .getDeclaredField("CREATOR")
                    .get(null) as Parcelable.Creator<*>

            return creator.createFromParcel(source) as ViewListHeaderVO
        }

        override fun newArray(size: Int): Array<ViewListHeaderVO?> {
            return arrayOfNulls(size)
        }
    }
}