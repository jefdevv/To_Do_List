package com.example.todolist.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.listener.TaskListener
import com.example.todolist.model.Task

class Adapter(private val taskList: List<Task>, private val taskListener: TaskListener) :
    RecyclerView.Adapter<ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount() = taskList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val task = taskList[position]

        holder.taskDescription.text = task.taskDescription
        holder.date.text = task.date
        holder.hour.text = task.hour
        if (task.checked) {
            holder.checkBx.isChecked = true
            holder.taskDescription.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.date.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.hour.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG


        }

        holder.checkBx.setOnClickListener() {
            taskListener.onClick(task)

        }

        holder.imgDelete.setOnClickListener() {
            taskListener.onDelete(task)
        }

        holder.itemView.setOnClickListener() {
            taskListener.onOpenEditMode(task)
        }
    }


}

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val taskDescription = view.findViewById<TextView>(R.id.txtTaskRow)
    val date = view.findViewById<TextView>(R.id.txtDateRow)
    val hour = view.findViewById<TextView>(R.id.txtHourRow)
    val checkBx = view.findViewById<CheckBox>(R.id.chkBoxRow)
    val imgDelete = view.findViewById<ImageView>(R.id.imgDeleteRow)

}
