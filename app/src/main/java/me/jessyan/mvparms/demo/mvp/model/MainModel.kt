package me.jessyan.mvparms.demo.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import io.reactivex.Observable
import me.jessyan.mvparms.demo.app.utils.net.NetworkUtils
import me.jessyan.mvparms.demo.mvp.contract.MainContract
import me.jessyan.mvparms.demo.mvp.model.entity.IndexMenuEntity
import javax.inject.Inject


@ActivityScope
class MainModel @Inject
constructor(repositoryManager: IRepositoryManager, private var mGson: Gson?, private var mApplication: Application?) : BaseModel(repositoryManager), MainContract.Model {

    override fun onDestroy() {
        super.onDestroy()
        this.mGson = null
        this.mApplication = null
    }

    override fun getMainMenu(maps: Map<String, String>): Observable<IndexMenuEntity> {
        return NetworkUtils.requestToObject(mRepositoryManager, maps, IndexMenuEntity::class.java)
    }
}