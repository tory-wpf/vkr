package com.example.vkr.presentation.stocks.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.vkr.presentation.stocks.view.StocksListFragment

class StocksViewPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragments = listOf<Fragment>(
        StocksListFragment().apply {
            val bundle = Bundle()
            bundle.putBoolean("No_request", false)
            arguments = bundle
        }, StocksListFragment().apply {
            val bundle = Bundle()
            bundle.putBoolean("No_request", true)
            arguments = bundle
        })

    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Stocks"
            else -> "Favourite"
        }
    }
}