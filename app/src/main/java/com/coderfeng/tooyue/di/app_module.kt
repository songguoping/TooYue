package com.coderfeng.tooyue.di


import com.coderfeng.tooyue.data.db.userDao
import com.coderfeng.tooyue.data.http.UserService
import com.coderfeng.tooyue.home.viewmodel.HomeViewModel
import com.coderfeng.tooyue.user.api.UserApi
import com.coderfeng.tooyue.user.repository.UserRepository
import com.coderfeng.tooyue.user.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { UserViewModel(get()) }
}

val reposModule = module {
    //factory 每次注入时都重新创建一个新的对象
    factory { UserRepository(get(), get()) }
}

val remoteModule = module {
    single<UserApi> { UserService }
}

val localModule = module {
    single { userDao }
}

val appModule = listOf(viewModelModule, reposModule, remoteModule, localModule)