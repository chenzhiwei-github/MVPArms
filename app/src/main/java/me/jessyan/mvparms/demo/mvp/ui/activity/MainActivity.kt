package me.jessyan.mvparms.demo.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.Preconditions.checkNotNull
import kotlinx.android.synthetic.main.activity_main.*
import me.jessyan.mvparms.demo.R
import me.jessyan.mvparms.demo.app.utils.net.NetworkUtils
import me.jessyan.mvparms.demo.di.component.DaggerMainComponent
import me.jessyan.mvparms.demo.di.module.MainModule
import me.jessyan.mvparms.demo.mvp.contract.MainContract
import me.jessyan.mvparms.demo.mvp.model.entity.IndexMenuEntity
import me.jessyan.mvparms.demo.mvp.presenter.MainPresenter
import me.jessyan.mvparms.demo.mvp.ui.adapter.MainAdapter


class MainActivity : BaseActivity<MainPresenter>(), MainContract.View {

    private var mAdapter: MainAdapter? = null

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        title = "云商户"
        return R.layout.activity_main //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    override fun initData(savedInstanceState: Bundle?) {
        initRecyclerView()
        initData()
    }

    private fun initData() {
        mPresenter!!.requestMainMenu()
    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        ArmsUtils.configRecyclerView(recyclerView, GridLayoutManager(this, 2))
        mAdapter = MainAdapter(mutableListOf())
        recyclerView.adapter = mAdapter
    }

    override fun onMainResponse(response: IndexMenuEntity) {
        mAdapter?.updateData(response.moudleList)
    }

    override fun showLoading() {
        NetworkUtils.showLoading()
    }

    override fun hideLoading() {
        NetworkUtils.hideLoading()
    }

    override fun showMessage(message: String) {
        checkNotNull(message)
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }

}
