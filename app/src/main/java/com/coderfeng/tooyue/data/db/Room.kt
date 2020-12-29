package com.coderfeng.tooyue.data.db

import androidx.room.Room
import com.coderfeng.tooyue.AppContext

private const val DB_NAME = "tooyue_db"

val room = Room.databaseBuilder(AppContext, AppDataBase::class.java, DB_NAME).build()

val userDao = room.getUserDao()