package com.example.todolist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todolist.dao.TaskDao
import com.example.todolist.model.Task

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        fun getDatabase(ctx: Context): TaskDatabase {
            val db = Room.databaseBuilder(
                ctx,
                TaskDatabase::class.java,
                "taskDataBase"
            ).build()
            return db
        }
    }
}