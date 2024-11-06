package com.example.lr_3

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: Grade)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Query("SELECT * FROM students")
    fun getAllStudents(): LiveData<List<Student>>

    @Query("SELECT * FROM grades WHERE studentId = :studentId")
    fun getGradesForStudent(studentId: Int): LiveData<List<Grade>>
}