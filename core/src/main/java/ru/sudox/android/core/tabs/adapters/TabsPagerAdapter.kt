package ru.sudox.android.core.tabs.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.sudox.android.core.CoreFragment
import ru.sudox.android.core.tabs.TabsChildFragment
import ru.sudox.android.core.tabs.TabsRootFragment

/**
 * Адаптер для ViewPager'а TabsRootFragment
 * Отвечает за инициализацию отображения фрагментов-вкладок и выдачу их названий
 *
 * @param fragments Дочерние фрагменты-вкладки
 * @param rootFragment Основной фрагмент
 */
class TabsPagerAdapter(
        private val fragments: Array<CoreFragment>,
        private val rootFragment: TabsRootFragment
) : FragmentStateAdapter(rootFragment) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    /**
     * Выдает заголовок фрагмента на определенной позиции
     *
     * @param position Позиция фрагмента, у которого нужно вернуть заголовок
     * @return Заголовок искомого фрагмента
     */
    fun getTitle(position: Int): String? {
        return (fragments[position] as TabsChildFragment).getTitle(rootFragment.context!!)
    }
}