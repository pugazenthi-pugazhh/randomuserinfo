package com.randomusers.roomdb.repo

import android.app.Application
import com.randomusers.roomdb.RandomUserDatabase
import com.randomusers.roomdb.model.UserDataDao
import com.randomusers.roomdb.model.UserDataTable
import com.randomusers.roomdb.subscribeOnBackground

class UserDataRepo(application: Application) {
    private var userDataDao: UserDataDao
    private val database = RandomUserDatabase.getInstance(application)

    init {
        userDataDao = database.usersDao()
    }
    fun insert(note: UserDataTable) {
        subscribeOnBackground {
            userDataDao.insert(note)
        }
    }
}