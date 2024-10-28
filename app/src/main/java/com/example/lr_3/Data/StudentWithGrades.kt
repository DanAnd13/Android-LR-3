package com.example.lr_3

import androidx.room.Embedded
import androidx.room.Relation

data class StudentWithGrades(
    @Embedded val student: Student,
    @Relation(parentColumn = "id", entityColumn = "studentId")
    val grades: List<Grade>
)
