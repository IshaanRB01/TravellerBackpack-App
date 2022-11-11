package com.example.travellerbackpack.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.travellerbackpack.fragments.MapsFragment
import com.example.travellerbackpack.fragments.ScanTextFragment
import com.example.travellerbackpack.fragments.TranslateTextFragment
import com.example.travellerbackpack.adapter.ViewPagerAdapter
import com.example.travellerbackpack.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    lateinit var viewPager2: ViewPager2

    lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager2 = binding.flFragment
        viewPager2.isUserInputEnabled = false
        viewPager2.setCurrentItem(0, false)

        binding.bottomNav.onItemSelected = {
            when (it) {
                0 -> viewPager2.setCurrentItem(0, false)
                1 -> viewPager2.setCurrentItem(1, false)
                2 -> viewPager2.setCurrentItem(2, false)
            }
        }

        setupViewPager(viewPager2)
    }

    private fun setupViewPager(viewPager: ViewPager2) {
        adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        val scanTextFragment = ScanTextFragment()
        val translateTextFragment = TranslateTextFragment()
        val mapsFragment = MapsFragment()
        adapter.addFragment(scanTextFragment)
        adapter.addFragment(translateTextFragment)
        adapter.addFragment(mapsFragment)
        viewPager.adapter = adapter
    }

}