package com.example.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "task")
class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskDescription: String,
    var checked: Boolean,
    val date: String,
    val hour: String

) : Serializable