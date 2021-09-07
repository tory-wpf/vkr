package com.example.vkr.presentation.stocks

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.vkr.databinding.ActivityMainBinding
import com.example.vkr.presentation.stocks.adapters.StocksViewPagerAdapter
import com.example.vkr.presentation.search.view.SearchStocksActivity

class MainActivity : AppCompatActivity(), IMainActivity {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.stocksViewPager.adapter = StocksViewPagerAdapter(supportFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.stocksViewPager)
        setListeners()
    }

    override fun getVP(): ViewPager = binding.stocksViewPager

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SearchStocksActivity.REQ_CODE && resultCode == Activity.RESULT_OK) {
            binding.stocksViewPager.currentItem = 0
        }
    }

    private fun setListeners(){
        binding.searchView.setOnClickListener {
            startActivityForResult(
                Intent(this, SearchStocksActivity::class.java),
                SearchStocksActivity.REQ_CODE
            )
        }
    }
}
