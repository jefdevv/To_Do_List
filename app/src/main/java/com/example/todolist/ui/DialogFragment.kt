package com.example.todolist.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.todolist.R
import com.example.todolist.databinding.FragmentDialogBinding
import com.example.todolist.db.TaskDatabase
import com.example.todolist.listener.TaskListener
import com.example.todolist.model.Task
import com.example.todolist.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DialogFragment : DialogFragment() {
    private lateinit var dataBase: TaskDatabase
    private lateinit var binding: FragmentDialogBinding
    private lateinit var listener: TaskListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        binding = FragmentDialogBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataBase = TaskDatabase.getDatabase(context)
        listener = context as TaskListener
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val currentDate = Calendar.getInstance()
        val dateDialog = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            currentDate.set(Calendar.YEAR, year)
            currentDate.set(Calendar.MONTH, month)
            currentDate.set(Calendar.DAY_OF_MONTH, day)
            taskDate(currentDate)
        }

        binding.edtDateDialog.setOnClickListener() {
            DatePickerDialog(
                view.context,
                dateDialog,
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.edtHourDialog.setOnClickListener() {
            taskHour(view.context)
        }


        if (arguments == null) {
            saveTaskDescription()
        } else {
            loadTask()
        }


    }


    private fun taskDate(calendar: Calendar) {
        val formatDate = Constants.TASK_DATE_FORMAT
        val simpleFormat = SimpleDateFormat(formatDate, Locale.ENGLISH)
        val taskTextDate = simpleFormat.format(calendar.time)
        binding.edtDateDialog.setText(taskTextDate)

    }

    private fun taskHour(context: Context) {
        val currentHour = Calendar.getInstance()
        val hour = currentHour.get(Calendar.HOUR_OF_DAY)
        val minute = currentHour.get(Calendar.MINUTE)

        TimePickerDialog(
            context, { _,
                       getHour,
                       getMinute ->
                val formatTime = android.text.format.DateFormat.getTimeFormat(context)
                val formatHour = formatTime.format(Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, getHour)
                    set(Calendar.MINUTE, getMinute)
                }.time)
                binding.edtHourDialog.setText(formatHour)
            }, hour, minute, true
        ).show()
    }

    private fun saveTaskDescription() {

        binding.btnSave.setOnClickListener() {
            if (binding.edtTaskDialog.text.isEmpty()) {
                binding.edtTaskDialog.error = getString(R.string.empty_field)
            } else {
                val edtTask = binding.edtTaskDialog.text.toString()
                val edtDate = binding.edtDateDialog.text.toString()
                val edtHour = binding.edtHourDialog.text.toString()

                GlobalScope.launch {
                    dataBase.taskDao()
                        .insert(
                            Task(
                                taskDescription = edtTask,
                                date = edtDate,
                                hour = edtHour,
                                checked = false
                            )
                        )
                }
                listener.onRefresh()
                dismiss()
            }
        }


    }


    private fun loadTask() {
        val bundle = arguments

        val task = bundle?.getSerializable(Constants.TASK) as Task
        val checked: Boolean = validateChecked(task)

        binding.edtTaskDialog.setText(task.taskDescription)
        binding.edtDateDialog.setText(task.date)
        binding.edtHourDialog.setText(task.hour)

        binding.btnSave.setOnClickListener() {
            GlobalScope.launch {
                dataBase.taskDao()
                    .update(
                        Task(
                            id = task.id,
                            taskDescription = binding.edtTaskDialog.text.toString(),
                            date = binding.edtDateDialog.text.toString(),
                            hour = binding.edtHourDialog.text.toString(),
                            checked = checked
                        )
                    )
            }
            listener.onRefresh()
            dismiss()
        }


    }

    private fun validateChecked(task: Task): Boolean {
        val checked: Boolean
        if (task.checked) {
            checked = true
        } else {

            checked = false
        }
        return checked
    }


}