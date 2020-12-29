package com.coderfeng.tooyue

import android.app.Application
import android.content.ContextWrapper
import com.coderfeng.tooyue.di.appModule
import org.koin.core.context.startKoin

lateinit var mApplication: Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mApplication = this
        startKoin {
            modules(appModule)
        }
//        //启动器进行异步初始化
//        TaskDispatcher.init(this)
//        TaskDispatcher.createInstance()
//            .addTask(InitBuGlyTask())
//            .addTask(InitKoInTask())
//            .addTask(InitImageLoaderTask())
//            .addTask(InitSmartRefreshLayoutTask())
//            .start()
    }
}

object AppContext : ContextWrapper(mApplication)//ContextWrapper对Context上下文进行包装(装饰者模式)
