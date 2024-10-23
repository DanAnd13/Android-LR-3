package com.example.lr_3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudentViewModel : ViewModel() {
    private val _studentsList = MutableLiveData<MutableList<Student>>().apply {
        value = mutableListOf()
    }
    val studentsList: LiveData<MutableList<Student>> = _studentsList
    var studentCounter = 1  // Лічильник студентів

    // Додавання студента до списку
    fun addStudent(student: Student) {
        _studentsList.value?.add(student)
        _studentsList.value = _studentsList.value  // Оновлюємо LiveData, щоб викликати спостерігачів
    }

    // Збільшення лічильника
    fun incrementCounter() {
        studentCounter++
    }

    // Відновлення лічильника на основі кількості студентів у списку
    fun restoreCounter() {
        studentCounter = _studentsList.value?.size?.plus(1) ?: 1
    }
}
