package com.wp.exposure

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.item_list.view.*

/**
 * create by WangPing
 * on 2020/11/6
 */
class ListActivity : AppCompatActivity() {
    private lateinit var adapter: ListAdapter
    private lateinit var recyclerViewExposureHelper: RecyclerViewExposureHelper<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        initData()
        srlRefreshLayout.setOnRefreshListener {
            Log.d("ListActivity", "开始刷新列表")
            srlRefreshLayout.postDelayed({
                //模拟两秒才请求完数据
                val list = ArrayList<String>()
                repeat(20) {
                    list.add("下拉刷新生成的数据$it")
                }
                adapter.setNewInstance(list)
                srlRefreshLayout.isRefreshing = false
                Log.d("ListActivity", "刷新数据更新完毕")
            }, 2 * 1000L)
        }
    }

    private fun initData() {
        val list = ArrayList<String>()
        repeat(20) {
            list.add("初始化生成的数据$it")
        }
        adapter = ListAdapter(list).apply {
            rvList.adapter = this
            loadMoreModule.setOnLoadMoreListener {
                Log.d("ListActivity", "开始加载更多数据")
                rvList.postDelayed({
                    //模拟两秒才请求完数据
                    val moreData = ArrayList<String>()
                    repeat(20) {
                        moreData.add("加载更多生成的数据$it")
                    }
                    adapter.addData(moreData)
                    Log.d("ListActivity", "加载更多数据添加完毕")
                }, 2 * 1000L)
            }
            setOnItemClickListener { adapter, _, position ->
                Log.d("ListActivity", "${adapter.data[position]}被删除了")
                adapter.removeAt(position)
            }
        }
        recyclerViewExposureHelper =
            RecyclerViewExposureHelper(
                recyclerView = rvList,
                exposureValidAreaPercent = 50,
                exposureStateChangeListener = object : IExposureStateChangeListener<String> {
                    override fun onExposureStateChange(
                        bindExposureData: String,
                        position: Int,
                        inExposure: Boolean
                    ) {
                        Log.i(
                            "ListActivity", "${bindExposureData}${
                            if (inExposure) {
                                "开始曝光"
                            } else {
                                "结束曝光"
                            }
                            }"
                        )
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        recyclerViewExposureHelper.onVisible()
    }

    override fun onPause() {
        super.onPause()
        recyclerViewExposureHelper.onInvisible()
    }
}

class ListAdapter(data: MutableList<String>) : LoadMoreModule,
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_list, data) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.itemView.apply {
            exposureRoot.exposureBindData = item
            tvListText.text = item
        }
    }
}