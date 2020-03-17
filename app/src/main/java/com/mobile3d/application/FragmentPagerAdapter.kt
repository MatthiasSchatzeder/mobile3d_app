package com.mobile3d.application

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * this class is used display fragments in a ViewPager
 */
class FragmentPagerAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {

    /**
     * all added fragments are store here
     */
    private var fragmentList = listOf<Fragment>()

    /**
     * has to be implemented
     * @return the fragment at a index
     */
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    /**
     * has to be implemented
     * @return number of fragments
     */
    override fun getItemCount(): Int {
        return fragmentList.count()
    }

    /**
     * function to add fragments to the list
     */
    fun addFragment(fragment: Fragment){
        fragmentList = fragmentList + fragment
    }
}