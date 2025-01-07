package com.example.todolist.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.db.TaskDatabase
import com.example.todolist.listener.TaskListener
import com.example.todolist.model.Task
import com.example.todolist.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), TaskListener {
    private lateinit var listTask: List<Task>
    private lateinit var dataBase: TaskDatabase
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataBase = TaskDatabase.getDatabase(applicationContext)


        listTask()
        openDialog()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun isDarkModeEnabled(): Boolean {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun toggleDarkMode() {
        val darkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
        )
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (isDarkModeEnabled()) {
            val item = menu.findItem(R.id.id_menu)
            item.title = getString(R.string.light_mode)
            return super.onPrepareOptionsMenu(menu)
        } else {
            return false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.id_menu -> {
                toggleDarkMode()
                return true
            }

            else -> {
                return onOptionsItemSelected(item)
                true
            }

        }
    }

    private fun openDialog() {
        binding.fab.setOnClickListener() {
            val dialog = DialogFragment()
            dialog.show(supportFragmentManager, dialog.tag)
        }
    }

    private fun listTask() {
        GlobalScope.launch {
            val getTaskList = dataBase.taskDao().getAll()
            runOnUiThread {
                getTaskList.observe(this@MainActivity, {
                    listTask = arrayListOf()
                    listTask = it
                    binding.recycler.setHasFixedSize(true)
                    val adapter = Adapter(listTask, this@MainActivity)
                    binding.recycler.layoutManager = LinearLayoutManager(applicationContext)
                    binding.recycler.adapter = adapter

                })
            }

        }
    }

    override fun onClick(task: Task) {

        if (task.checked) {
            getCheck(false, task)
        } else {
            getCheck(true, task)
        }


        onRefresh()


    }

    private fun getCheck(checked: Boolean, task: Task) {
        GlobalScope.launch {

            dataBase.taskDao().update(
                Task(
                    id = task.id,
                    taskDescription = task.taskDescription,
                    date = task.date,
                    hour = task.hour,
                    checked = checked
                )
            )
        }
    }

    override fun onDelete(task: Task) {
        GlobalScope.launch {
            dataBase.taskDao().delete(task)

        }
    }

    override fun onRefresh() {
        listTask()
    }

    override fun onOpenEditMode(task: Task) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.TASK, task)
        val dialog = DialogFragment()
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, dialog.tag)
    }


}
