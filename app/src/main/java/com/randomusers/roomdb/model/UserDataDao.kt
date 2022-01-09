package com.randomusers.roomdb.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: UserDataTable)
}