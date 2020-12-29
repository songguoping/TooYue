package com.coderfeng.tooyue.home.viewmodel

import androidx.lifecycle.LiveData
import com.coderfeng.tooyue.base.viewmodel.BaseViewModel
import com.coderfeng.tooyue.home.model.ReleaseModel
import com.coderfeng.tooyue.user.repository.UserRepository

class HomeViewModel(private val mUserRepository: UserRepository) : BaseViewModel() {

    fun deleteUser() {
        launch {
            mUserRepository.deleteLocalUser(mUserRepository.getLocalUsers()[0])
        }
    }

    fun getReleases(): LiveData<ReleaseModel> = emit {
        mUserRepository.getReleases()[0]
    }
}
