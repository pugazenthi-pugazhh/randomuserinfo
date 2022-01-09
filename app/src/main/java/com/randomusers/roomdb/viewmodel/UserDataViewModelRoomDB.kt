package com.randomusers.roomdb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.randomusers.roomdb.model.UserDataTable
import com.randomusers.roomdb.repo.UserDataRepo

class UserDataViewModelRoomDB(app: Application) : AndroidViewModel(app) {
    private val repository = UserDataRepo(app)
    fun insert(note: UserDataTable) {
        repository.insert(note)
    }

}