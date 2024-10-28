package com.example.lr_3

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MyAdapter
    private lateinit var studentDao: StudentDao
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = AppDatabase.getDatabase(this)
        studentDao = db.studentDao()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val addButton: Button = findViewById(R.id.new_student)
        val deleteButton: Button = findViewById(R.id.delete_student)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(studentList, mutableMapOf())
        recyclerView.adapter = adapter

        studentDao.getAllStudents().observe(this, Observer { students ->
            studentList.clear()
            studentList.addAll(students)
            adapter.updateData(studentList)
            loadGradesForStudents()
        })

        addButton.setOnClickListener {
            addNewStudent()
        }

        deleteButton.setOnClickListener {
            deleteStudentWithLowestAverage()
        }
    }

    private fun deleteStudentWithLowestAverage() {
        lifecycleScope.launch {
            val studentWithLowestAverage = studentDao.getStudentWithLowestAverage()

            if (studentWithLowestAverage != null) {
                studentDao.deleteStudent(studentWithLowestAverage.student)

                loadStudentsFromDatabase()
            } else {
                Log.d("StudentList", "No students found for deletion")
            }
        }
    }

    private fun addNewStudent() {
        lifecycleScope.launch {
            val newStudent = Student(0, "FirstName", "LastName", "Group")

            val studentId = studentDao.insertStudent(newStudent).toInt()

            val updatedStudent = newStudent.copy(
                id = studentId,
                firstName = "${newStudent.firstName} $studentId",
                lastName = "${newStudent.lastName} $studentId"
            )
            studentDao.updateStudent(updatedStudent)

            generateGradesForStudent(studentId)

            loadStudentsFromDatabase()
        }
    }

    private fun generateGradesForStudent(studentId: Int) {
        val subjects = listOf("Math", "English")
        lifecycleScope.launch {
            val studentGrades = mutableListOf<Grade>()
            subjects.forEach { subject ->
                val gradeValue = Random.nextInt(60, 101)
                val grade = Grade(0, studentId, subject, gradeValue)
                try {
                    studentDao.insertGrade(grade)
                    studentGrades.add(grade)
                } catch (e: Exception) {
                    Log.e("Database Error", "Failed to insert grade: $e")
                }
            }
            adapter.gradesMap[studentId] = studentGrades
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadStudentsFromDatabase() {
        lifecycleScope.launch {
            val studentsFromDb = studentDao.getAllStudents().value ?: emptyList()
            studentList.clear()
            studentList.addAll(studentsFromDb)

            studentsFromDb.forEach { student ->
                val grades = studentDao.getGradesForStudent(student.id).value ?: emptyList()
                adapter.gradesMap[student.id] = grades
            }

            adapter.updateData(studentList)
        }
    }

    private fun loadGradesForStudents() {
        studentList.forEach { student ->
            lifecycleScope.launch {
                studentDao.getGradesForStudent(student.id).observe(this@MainActivity, Observer { grades ->
                    adapter.updateGrades(student.id, grades)
                })
            }
        }
    }
}