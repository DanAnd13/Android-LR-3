package com.example.lr_3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grades")
data class Grade(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int, // Foreign Key to Student
    val subject: String,
    val grade: Int
)