package com.example.lr_3

data class StudentResponse(
    val student: Student,
    val grades: Map<String, Int>
)