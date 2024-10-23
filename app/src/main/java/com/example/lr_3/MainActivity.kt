package com.example.lr_3

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Налаштування padding для edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ініціалізація RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        val addButton = findViewById<Button>(R.id.new_student)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Додавання розділювачів між елементами
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // Ініціалізація ViewModel
        studentViewModel = ViewModelProvider(this).get(StudentViewModel::class.java)

        // Створення адаптера та підключення його до RecyclerView
        adapter = MyAdapter(mutableListOf())
        recyclerView.adapter = adapter

        // Спостереження за змінами в студентському списку
        studentViewModel.studentsList.observe(this) { updatedList ->
            adapter.updateData(updatedList)
        }

        // Відновлення лічильника студентів при обертанні екрану
        if (studentViewModel.studentsList.value.isNullOrEmpty()) {
            populateInitialData()  // Додавання початкових даних тільки при першому запуску
        } else {
            // Відновлюємо лічильник після перезавантаження
            studentViewModel.restoreCounter()
        }

        // Обробка натискання кнопки для додавання нового студента
        addButton.setOnClickListener {
            addNewStudent()
        }
    }

    // Заповнення початковими даними через ViewModel
    private fun populateInitialData() {
        for (i in 1..5) {
            studentViewModel.addStudent(Student("FirstName$i", "LastName$i", "Group$i"))
        }
        studentViewModel.studentCounter = 6 // Починаємо лічильник з 6, оскільки 5 студентів уже додано
    }

    // Метод для додавання нового студента через ViewModel
    private fun addNewStudent() {
        val newStudent = Student("FirstName${studentViewModel.studentCounter}", "LastName${studentViewModel.studentCounter}", "Group${studentViewModel.studentCounter}")
        studentViewModel.addStudent(newStudent)
        studentViewModel.incrementCounter()
    }
}
