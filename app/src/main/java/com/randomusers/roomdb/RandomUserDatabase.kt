package com.randomusers.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.randomusers.roomdb.model.UserDataDao
import com.randomusers.roomdb.model.UserDataTable

@Database(
    entities = [UserDataTable::class],
    version = 1
)
abstract class RandomUserDatabase: RoomDatabase() {
    abstract fun usersDao(): UserDataDao


    companion object {
        private var instance: RandomUserDatabase? = null
        lateinit var mContext: Context

        @Synchronized
        fun getInstance(ctx: Context): RandomUserDatabase {
            if (instance == null)
                mContext = ctx
            instance = Room.databaseBuilder(
                ctx.applicationContext, RandomUserDatabase::class.java,
                "randomusers"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .addCallback(roomCallback)
                .build()

            return instance!!

        }

        private val roomCallback = object : Callback() {
        }


    }
}