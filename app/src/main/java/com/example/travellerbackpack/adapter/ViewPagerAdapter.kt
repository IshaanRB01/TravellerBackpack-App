package com.example.travellerbackpack.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fragmentManager: FragmentManager, b: Lifecycle) :
    FragmentStateAdapter(fragmentManager, b){
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }
}