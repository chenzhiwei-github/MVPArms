package me.jessyan.mvparms.demo.di.module

import com.jess.arms.di.scope.ActivityScope
import dagger.Module
import dagger.Provides
import me.jessyan.mvparms.demo.mvp.contract.MainContract
import me.jessyan.mvparms.demo.mvp.model.MainModel


@Module
class MainModule
/**
 * 构建MainModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
 *
 * @param view
 */
(private val view: MainContract.View) {

    @ActivityScope
    @Provides
    internal fun provideMainView(): MainContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    internal fun provideMainModel(model: MainModel): MainContract.Model {
        return model
    }
}