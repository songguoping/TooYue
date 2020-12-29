package com.coderfeng.tooyue.user.repository

import com.coderfeng.tooyue.config.Configs
import com.coderfeng.tooyue.home.model.ReleaseModel
import com.coderfeng.tooyue.user.api.UserApi
import com.coderfeng.tooyue.user.dao.UserDao
import com.coderfeng.tooyue.user.model.db.User

class UserRepository(private val mUserApi: UserApi, private val mUserDao: UserDao) {

    suspend fun saveLocalUser(user: User) = mUserDao.insertAll(user)

    suspend fun getLocalUsers(): List<User> = mUserDao.getAll()

    suspend fun deleteLocalUser(user: User) = mUserDao.deleteAll(user)

    suspend fun getReleases(): List<ReleaseModel> =
        mUserApi.getReleases(Configs.OWNER, Configs.OWNER_REPO, 1)
}
