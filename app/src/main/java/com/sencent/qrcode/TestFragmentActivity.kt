package com.sencent.qrcode

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity

/**
 *  Create by Logan at 2018/12/21 0021
 *
 */
class TestFragmentActivity : AppCompatActivity() {
    private lateinit var mTabs: TabLayout
    private lateinit var mViewpager: ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_fragment)
        mTabs = findViewById(R.id.tabs)
        mViewpager = findViewById(R.id.viewpager)
        mViewpager.offscreenPageLimit=0
        mViewpager.adapter = TestPagerAdapter(this@TestFragmentActivity, supportFragmentManager)
        mTabs.setupWithViewPager(mViewpager)
    }

}