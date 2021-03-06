package com.yechaoa.wanandroid_kotlin.module.home

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yechaoa.wanandroid_kotlin.R
import com.yechaoa.wanandroid_kotlin.adapter.ArticleAdapter
import com.yechaoa.wanandroid_kotlin.base.BaseBean
import com.yechaoa.wanandroid_kotlin.base.BaseFragment
import com.yechaoa.wanandroid_kotlin.bean.Article
import com.yechaoa.wanandroid_kotlin.bean.Banner
import com.yechaoa.wanandroid_kotlin.module.detail.DetailActivity
import com.yechaoa.wanandroid_kotlin.utils.GlideImageLoader
import com.yechaoa.yutilskt.ToastUtilKt
import com.yechaoa.yutilskt.YUtilsKt
import com.youth.banner.BannerConfig
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : BaseFragment(), IHomeView, OnBannerListener {

    lateinit var mHomePresenter: HomePresenter
    private lateinit var bannerList: List<Banner>

    override fun createPresenter() {
        mHomePresenter = HomePresenter(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun initView() {
        recycler_view.layoutManager = LinearLayoutManager(mContext)
    }

    override fun initData() {
        mHomePresenter.getBanner()
        mHomePresenter.getArticleList(0)
    }

    override fun getBanner(banners: BaseBean<MutableList<Banner>>) {
        bannerList = banners.data

        val images: MutableList<String> = ArrayList()
        val titles: MutableList<String> = ArrayList()
        for (i in banners.data.indices) {
            images.add(banners.data[i].imagePath)
            titles.add(banners.data[i].title)
        }

        //动态设置高度
        val layoutParams = banner.layoutParams
        layoutParams.height = (YUtilsKt.getScreenWidth() / 1.8).roundToInt()

        banner
            .setImages(images)
            .setBannerTitles(titles)
            .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
            .setImageLoader(GlideImageLoader())
            .start()

        banner.setOnBannerListener(this)
    }

    override fun getBannerError(msg: String) {
        ToastUtilKt.showCenterToast(msg)
    }

    override fun getArticleList(article: BaseBean<Article>) {
        val datas = article.data.datas
        val articleAdapter = ArticleAdapter(datas)
        articleAdapter.animationEnable = true

        //item点击事件
        articleAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(mContext, DetailActivity::class.java)
            intent.putExtra(DetailActivity.WEB_URL, datas[position].link)
            intent.putExtra(DetailActivity.WEB_TITLE, datas[position].title)
            startActivity(intent)
        }

        //item子view点击事件
        articleAdapter.setOnItemChildClickListener { adapter, view, position ->
            ToastUtilKt.showCenterToast("收藏$position")
        }

        recycler_view.adapter = articleAdapter
    }

    override fun getArticleError(msg: String) {
        ToastUtilKt.showCenterToast(msg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //结束轮播
        banner.stopAutoPlay()
    }

    override fun OnBannerClick(position: Int) {
        val intent = Intent(mContext, DetailActivity::class.java)
        intent.putExtra(DetailActivity.WEB_URL, bannerList[position].url)
        intent.putExtra(DetailActivity.WEB_TITLE, bannerList[position].title)
        startActivity(intent)
    }

}

