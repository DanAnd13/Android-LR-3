package com.example.lr_3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private var studentList: MutableList<Student>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstName: TextView = view.findViewById(R.id.first_name)
        val lastName: TextView = view.findViewById(R.id.last_name)
        val group: TextView = view.findViewById(R.id.student_group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.student_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = studentList[position]
        holder.firstName.text = student.FirstName
        holder.lastName.text = student.LastName
        holder.group.text = student.Group
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    // Оновлення даних у адаптері
    fun updateData(newList: MutableList<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }
}
