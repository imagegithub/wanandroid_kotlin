package com.yechaoa.wanandroid_kotlin.module

import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.snackbar.Snackbar
import com.yechaoa.wanandroid_kotlin.R
import com.yechaoa.wanandroid_kotlin.adapter.CommonViewPagerAdapter
import com.yechaoa.wanandroid_kotlin.base.BaseActivity
import com.yechaoa.wanandroid_kotlin.common.MyConfig
import com.yechaoa.wanandroid_kotlin.module.home.HomeFragment
import com.yechaoa.wanandroid_kotlin.module.login.LoginActivity
import com.yechaoa.wanandroid_kotlin.module.navi.NaviFragment
import com.yechaoa.wanandroid_kotlin.module.project.ProjectFragment
import com.yechaoa.wanandroid_kotlin.module.tree.TreeFragment
import com.yechaoa.yutilskt.ActivityUtilKt
import com.yechaoa.yutilskt.SpUtilKt
import com.yechaoa.yutilskt.ToastUtilKt
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity() {

    override fun createPresenter() {}

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {

        toolbar.title = resources.getString(R.string.app_name)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            Snackbar.make(it, "这是一个提示", Snackbar.LENGTH_SHORT)
                .setAction("按钮") { ToastUtilKt.showCenterToast("点击了按钮") }.show()
        }

        initActionBarDrawer()

        initFragments()

    }

    /**
     * Drawer关联Toolbar
     */
    private fun initActionBarDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    /**
     * 初始化Fragment
     */
    private fun initFragments() {
        val viewPagerAdapter = CommonViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(HomeFragment())
        viewPagerAdapter.addFragment(TreeFragment())
        viewPagerAdapter.addFragment(NaviFragment())
        viewPagerAdapter.addFragment(ProjectFragment())

        view_pager.offscreenPageLimit = 1
        view_pager.adapter = viewPagerAdapter
    }

    override fun onResume() {
        super.onResume()
        setBadge()
        initListener()
    }

    /**
     * 给BottomNavigationView 设置Badge 小红点
     *
     * BottomNavigationMenuView中的每一个Tab是一个FrameLayout，所以可以在上面随意添加View、这样就可以实现角标了
     */
    private fun setBadge() {
        //获取底部菜单view
        val menuView = bottom_navigation.getChildAt(0) as BottomNavigationMenuView
        //获取第2个itemView
        val itemView = menuView.getChildAt(1) as BottomNavigationItemView
        //引入badgeView
        val badgeView =
            LayoutInflater.from(this).inflate(R.layout.layout_badge_view, menuView, false)
        //把badgeView添加到itemView中
        itemView.addView(badgeView)
        //获取子view并设置显示数目
        val count = badgeView.findViewById<TextView>(R.id.tv_badge)
        count.text = "2"

        //不显示则隐藏
        //count.visibility=View.GONE
    }

    private fun initListener() {
        /**
         * 侧边栏点击事件
         */
        nav_view.setNavigationItemSelectedListener {
            // Handle navigation view item clicks here.
            when (it.itemId) {
                R.id.nav_collect -> {
                    ToastUtilKt.showCenterToast("收藏")
//                    startActivity(Intent(this, CollectActivity::class.java))
                }
                R.id.nav_share -> {
                    ToastUtilKt.showCenterToast("分享")
                }
                R.id.nav_about -> {
                    ToastUtilKt.showCenterToast("关于")
//                    startActivity(Intent(this, AboutActivity::class.java))
                }
                R.id.nav_logout -> {
                    //todo dialog提示
                    SpUtilKt.setBoolean(MyConfig.IS_LOGIN, false)
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }

            //关闭侧边栏
            drawer_layout.closeDrawer(GravityCompat.START)

            true
        }

        /**
         * view_pager 滑动监听
         */
        view_pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                bottom_navigation.menu.getItem(position).isChecked = true
                //设置checked为true，但是不能触发ItemSelected事件，所以滑动时也要设置一下标题
                when (position) {
                    0 -> {
                        toolbar.title = resources.getString(R.string.app_name)
                    }
                    1 -> {
                        toolbar.title = resources.getString(R.string.title_tree)
                    }
                    2 -> {
                        toolbar.title = resources.getString(R.string.title_navi)
                    }
                    else -> {
                        toolbar.title = resources.getString(R.string.title_project)
                    }
                }
            }
        })

        /**
         * bottom_navigation 点击事件
         */
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    view_pager.currentItem = 0
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_tree -> {
                    view_pager.currentItem = 1
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_navi -> {
                    view_pager.currentItem = 2
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_project -> {
                    view_pager.currentItem = 3
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    /**
     * 添加toolbar菜单
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * toolbar菜单事件
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_search -> {
                ToastUtilKt.showCenterToast("搜索")
            }
            R.id.action_settings -> {
                ToastUtilKt.showCenterToast("设置")
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private var mExitTime: Long = 0 // 保存用户按返回键的时间

    /**
     * 拦截返回事件，自处理
     */
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtilKt.showCenterToast("再按一次退出" + resources.getString(R.string.app_name))
                mExitTime = System.currentTimeMillis()
            } else {
                ActivityUtilKt.closeAllActivity()
            }
        }
        //super.onBackPressed()
    }

}
