package com.randomusers.roomdb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class UserDataTable (
    val firstName: String,
    val lastName: String,
    val email: String,
    val dob: String,
    val phone: String,
    val imagePath: String,
    @PrimaryKey(autoGenerate = false) val id: Int? = null
)