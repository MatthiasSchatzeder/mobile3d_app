package com.example.myapplication

/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

import java.lang.ref.WeakReference

/**
 * A mediator to link a TabLayout with a ViewPager2. The mediator will synchronize the ViewPager2's
 * position with the selected tab when a tab is selected, and the TabLayout's scroll position when
 * the user drags the ViewPager2.
 *
 *
 * Establish the link by creating an instance of this class, make sure the ViewPager2 has an
 * adapter and then call [.attach] on it. When creating an instance of this class, you must
 * supply an implementation of [OnConfigureTabCallback] in which you set the text of the tab,
 * and/or perform any styling of the tabs that you require.
 */
class TabLayoutMediator(
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager2,
    private val autoRefresh: Boolean,
    private val onConfigureTabCallback: OnConfigureTabCallback
) {
    private var adapter: RecyclerView.Adapter<*>? = null
    private var attached: Boolean = false

    private var onPageChangeCallback: TabLayoutOnPageChangeCallback? = null
    private var onTabSelectedListener: TabLayout.OnTabSelectedListener? = null
    private var pagerAdapterObserver: RecyclerView.AdapterDataObserver? = null

    /**
     * A callback interface that must be implemented to set the text and styling of newly created
     * tabs.
     */
    interface OnConfigureTabCallback {
        /**
         * Called to configure the tab for the page at the specified position. Typically calls [ ][TabLayout.Tab.setText], but any form of styling can be applied.
         *
         * @param tab The Tab which should be configured to represent the title of the item at the given
         * position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        fun onConfigureTab(tab: TabLayout.Tab, position: Int)
    }

    constructor(
        tabLayout: TabLayout,
        viewPager: ViewPager2,
        onConfigureTabCallback: OnConfigureTabCallback
    ) : this(tabLayout, viewPager, true, onConfigureTabCallback) {
    }

    /**
     * Link the TabLayout and the ViewPager2 together.
     *
     * @throws IllegalStateException If the mediator is already attached, or the ViewPager2 has no
     * adapter.
     */
    fun attach() {
        if (attached) {
            throw IllegalStateException("TabLayoutMediator is already attached")
        }
        adapter = viewPager.adapter
        if (adapter == null) {
            throw IllegalStateException(
                "TabLayoutMediator attached before ViewPager2 has an " + "adapter"
            )
        }
        attached = true

        // Add our custom OnPageChangeCallback to the ViewPager
        onPageChangeCallback = TabLayoutOnPageChangeCallback(tabLayout)
        viewPager.registerOnPageChangeCallback(onPageChangeCallback!!)

        // Now we'll add a tab selected listener to set ViewPager's current item
        onTabSelectedListener = ViewPagerOnTabSelectedListener(viewPager)
        tabLayout.addOnTabSelectedListener(onTabSelectedListener!!)

        // Now we'll populate ourselves from the pager adapter, adding an observer if
        // autoRefresh is enabled
        if (autoRefresh) {
            // Register our observer on the new adapter
            pagerAdapterObserver = PagerAdapterObserver()
            adapter!!.registerAdapterDataObserver(pagerAdapterObserver!!)
        }

        populateTabsFromPagerAdapter()

        // Now update the scroll position to match the ViewPager's current item
        tabLayout.setScrollPosition(viewPager.currentItem, 0f, true)
    }

    /** Unlink the TabLayout and the ViewPager  */
    fun detach() {
        adapter!!.unregisterAdapterDataObserver(pagerAdapterObserver!!)
        tabLayout.removeOnTabSelectedListener(onTabSelectedListener!!)
        viewPager.unregisterOnPageChangeCallback(onPageChangeCallback!!)
        pagerAdapterObserver = null
        onTabSelectedListener = null
        onPageChangeCallback = null
        attached = false
    }

    internal fun populateTabsFromPagerAdapter() {
        tabLayout.removeAllTabs()

        if (adapter != null) {
            val adapterCount = adapter!!.itemCount
            for (i in 0 until adapterCount) {
                val tab = tabLayout.newTab()
                onConfigureTabCallback.onConfigureTab(tab, i)
                tabLayout.addTab(tab, false)
            }

            // Make sure we reflect the currently set ViewPager item
            if (adapterCount > 0) {
                val currItem = viewPager.currentItem
                if (currItem != tabLayout.selectedTabPosition) {
                    tabLayout.getTabAt(currItem)!!.select()
                }
            }
        }
    }

    /**
     * A [ViewPager2.OnPageChangeCallback] class which contains the necessary calls back to the
     * provided [TabLayout] so that the tab position is kept in sync.
     *
     *
     * This class stores the provided TabLayout weakly, meaning that you can use [ ][ViewPager2.registerOnPageChangeCallback] without removing the
     * callback and not cause a leak.
     */
    private class TabLayoutOnPageChangeCallback internal constructor(tabLayout: TabLayout) :
        ViewPager2.OnPageChangeCallback() {
        private val tabLayoutRef: WeakReference<TabLayout>
        private var previousScrollState: Int = 0
        private var scrollState: Int = 0

        init {
            tabLayoutRef = WeakReference(tabLayout)
            reset()
        }

        override fun onPageScrollStateChanged(state: Int) {
            previousScrollState = scrollState
            scrollState = state
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val tabLayout = tabLayoutRef.get()
            if (tabLayout != null) {
                // Only update the text selection if we're not settling, or we are settling after
                // being dragged
                val updateText = scrollState != SCROLL_STATE_SETTLING || previousScrollState == SCROLL_STATE_DRAGGING
                // Update the indicator if we're not settling after being idle. This is caused
                // from a setCurrentItem() call and will be handled by an animation from
                // onPageSelected() instead.
                val updateIndicator =
                    !(scrollState == SCROLL_STATE_SETTLING && previousScrollState == SCROLL_STATE_IDLE)
                tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator)
            }
        }

        override fun onPageSelected(position: Int) {
            val tabLayout = tabLayoutRef.get()
            if (tabLayout != null
                && tabLayout.selectedTabPosition != position
                && position < tabLayout.tabCount
            ) {
                // Select the tab, only updating the indicator if we're not being dragged/settled
                // (since onPageScrolled will handle that).
                val updateIndicator =
                    scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_SETTLING && previousScrollState == SCROLL_STATE_IDLE
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator)
            }
        }

        internal fun reset() {
            scrollState = SCROLL_STATE_IDLE
            previousScrollState = scrollState
        }
    }

    /**
     * A [TabLayout.OnTabSelectedListener] class which contains the necessary calls back to the
     * provided [ViewPager2] so that the tab position is kept in sync.
     */
    private class ViewPagerOnTabSelectedListener internal constructor(private val viewPager: ViewPager2) :
        TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab) {
            viewPager.setCurrentItem(tab.position, true)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            // No-op
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            // No-op
        }
    }

    private inner class PagerAdapterObserver internal constructor() : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }
    }
}