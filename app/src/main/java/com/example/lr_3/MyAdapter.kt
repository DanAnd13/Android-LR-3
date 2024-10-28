package com.example.lr_3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(
    private var students: List<Student>,
    val gradesMap: MutableMap<Int, List<Grade>>
) : RecyclerView.Adapter<MyAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstNameText: TextView = view.findViewById(R.id.first_name)
        val lastNameText: TextView = view.findViewById(R.id.last_name)
        val groupText: TextView = view.findViewById(R.id.student_group)
        val gradesText: TextView = view.findViewById(R.id.student_grade)

        fun bind(student: Student) {
            firstNameText.text = student.firstName
            lastNameText.text = student.lastName
            groupText.text = student.group

            val studentGrades = gradesMap[student.id] ?: emptyList()
            gradesText.text = studentGrades.joinToString(", ") { "${it.subject}: ${it.grade}" }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_list, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size

    fun updateData(newData: List<Student>) {
        students = newData
        notifyDataSetChanged()
    }

    fun updateGrades(studentId: Int, grades: List<Grade>) {
        gradesMap[studentId] = grades
        notifyDataSetChanged()
    }
}