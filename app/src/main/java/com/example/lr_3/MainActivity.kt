package com.example.lr_3

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MyAdapter
    private lateinit var studentDao: StudentDao
    private lateinit var studentApiService: StudentApiService
    private val studentList = mutableListOf<Student>()
    private var currentStudentId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = AppDatabase.getDatabase(this)
        studentDao = db.studentDao()
        adapter = MyAdapter(studentList, mutableMapOf(), ::deleteStudent)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val addButton: Button = findViewById(R.id.new_student)

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://4befe232-b96a-410d-bd27-d434169f7c3d.mock.pstmn.io/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        studentApiService = retrofit.create(StudentApiService::class.java)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

            loadCurrentStudentId()
        loadStudentsFromDatabase()

        addButton.setOnClickListener {
            fetchStudentById(currentStudentId)
        }
    }

    private fun fetchStudentById(studentId: Int) {
        studentApiService.getStudent(studentId).enqueue(object : retrofit2.Callback<StudentResponse> {
            override fun onResponse(call: Call<StudentResponse>, response: Response<StudentResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { studentResponse ->
                        if (studentResponse.student.id == studentId) {
                            Log.d("Student", "Student: ${studentResponse.student.firstName} ${studentResponse.student.lastName}")

                            lifecycleScope.launch {
                                studentDao.insertStudent(studentResponse.student)

                                studentResponse.grades.forEach { (subject, grade) ->
                                    val gradeEntity = Grade(studentId = studentResponse.student.id, subject = subject, grade = grade)
                                    studentDao.insertGrade(gradeEntity)
                                }

                                currentStudentId++
                                saveCurrentStudentId()
                            }
                        } else {
                            Log.e("API Error", "ID mismatch: Expected $studentId, got ${studentResponse.student.id}")
                            Toast.makeText(this@MainActivity, "Student with ID $studentId not found.", Toast.LENGTH_SHORT).show()
                            currentStudentId++
                            saveCurrentStudentId()
                        }
                    }
                } else {
                    Log.e("API Error", "Error fetching student data: ${response.message()}")
                    Toast.makeText(this@MainActivity, "Student with ID $studentId not found.", Toast.LENGTH_SHORT).show()
                    currentStudentId++
                    saveCurrentStudentId()
                }
            }

            override fun onFailure(call: Call<StudentResponse>, t: Throwable) {
                Log.e("API Error", "Failed to fetch student: ${t.message}")
                Toast.makeText(this@MainActivity, "Failed to fetch student: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteStudent(student: Student) {
        lifecycleScope.launch {
            studentDao.deleteStudent(student)
            Log.d("StudentList", "Deleted student: ${student.firstName} ${student.lastName}")
        }
    }

    private fun loadStudentsFromDatabase() {
        lifecycleScope.launch {
            studentDao.getAllStudents().observe(this@MainActivity, Observer { students ->
                studentList.clear()
                studentList.addAll(students)
                adapter.updateData(studentList)

                loadGradesForStudents()
            })
        }
    }

    private fun loadGradesForStudents() {
        studentList.forEach { student ->
            lifecycleScope.launch {
                studentDao.getGradesForStudent(student.id).observe(this@MainActivity, Observer { grades ->
                    grades?.let {
                        adapter.updateGrades(student.id, it)
                    } ?: Log.e("Database Error", "Grades for student ${student.firstName} not found")
                })
            }
        }
    }

    private fun loadCurrentStudentId() {
        val sharedPref = getSharedPreferences("student_app_prefs", Context.MODE_PRIVATE)
        currentStudentId = sharedPref.getInt("currentStudentId", 1)
    }

    private fun saveCurrentStudentId() {
        val sharedPref = getSharedPreferences("student_app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("currentStudentId", currentStudentId)
            apply()
        }
    }
}