package me.jessyan.mvparms.demo.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.ActivityScope

import dagger.Component
import me.jessyan.mvparms.demo.di.module.MainModule
import me.jessyan.mvparms.demo.mvp.ui.activity.MainActivity

@ActivityScope
@Component(modules = arrayOf(MainModule::class), dependencies = arrayOf(AppComponent::class))
interface MainComponent {
    fun inject(activity: MainActivity)
}