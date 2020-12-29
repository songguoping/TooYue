package com.coderfeng.tooyue.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.coderfeng.tooyue.base.viewmodel.BaseViewModel
import com.coderfeng.tooyue.user.model.UserInfoModel
import com.coderfeng.tooyue.user.model.db.User
import com.coderfeng.tooyue.user.repository.UserRepository


class UserViewModel(private val mUserRepository: UserRepository) : BaseViewModel() {

    //提供给xml文件进行绑定
    val mUserInfoModel = MutableLiveData<UserInfoModel>()



    fun saveLocalUser(user: User) {
        launch {
            mUserRepository.saveLocalUser(user)
        }
    }


}
