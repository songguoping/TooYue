package com.coderfeng.tooyue.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.coderfeng.tooyue.user.dao.UserDao
import com.coderfeng.tooyue.user.model.db.User

@Database(entities = [User::class], version = 1,exportSchema = false)
abstract class AppDataBase : RoomDatabase(){

    abstract fun getUserDao(): UserDao
}