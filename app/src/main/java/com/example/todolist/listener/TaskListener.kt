package com.example.todolist.listener

import com.example.todolist.model.Task

interface TaskListener {
    fun onClick(task: Task)
    fun onDelete(task: Task)
    fun onRefresh()
    fun onOpenEditMode(task: Task)
}