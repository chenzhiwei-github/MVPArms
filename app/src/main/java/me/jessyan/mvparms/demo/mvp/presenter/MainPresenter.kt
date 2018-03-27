package me.jessyan.mvparms.demo.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.utils.RxLifecycleUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.mvparms.demo.app.ApiConfiguration
import me.jessyan.mvparms.demo.mvp.contract.MainContract
import me.jessyan.mvparms.demo.mvp.model.entity.IndexMenuEntity
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import java.util.*
import javax.inject.Inject

@ActivityScope
class MainPresenter @Inject
constructor(model: MainContract.Model, rootView: MainContract.View, private var mErrorHandler: RxErrorHandler?, private var mApplication: Application?, private var mImageLoader: ImageLoader?, private var mAppManager: AppManager?) : BasePresenter<MainContract.Model, MainContract.View>(model, rootView) {

    override fun onDestroy() {
        super.onDestroy()
        this.mErrorHandler = null
        this.mAppManager = null
        this.mImageLoader = null
        this.mApplication = null
    }

    fun requestMainMenu() {
        val maps = HashMap<String, String>()
        maps["method"] = ApiConfiguration.Method.INDEX_MENU
        maps["pageType"] = "android_index"

        mModel.getMainMenu(maps)
                .subscribeOn(Schedulers.io())
//                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe { disposable -> mRootView.showLoading() } //显示进度条
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { mRootView.hideLoading() } //隐藏进度条
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<IndexMenuEntity>(mErrorHandler!!) {
                    override fun onNext(response: IndexMenuEntity) {
                        mRootView.onMainResponse(response)
                    }
                })
    }

}
