package com.faiza.todo.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todoFaizaAkabli.R
import com.example.todoFaizaAkabli.databinding.ActivityFormBinding
import com.faiza.todo.tasklist.Task
import java.util.*

class FormActivity : AppCompatActivity() {
    private var _binding: ActivityFormBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        _binding = ActivityFormBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val btn = binding.button
        val task = intent.getSerializableExtra("task") as? Task


        val editTextTitle = binding.title
        editTextTitle.setText(task?.title)

        val editTextDescription = binding.description
        editTextDescription.setText(task?.description)

        val id = task?.id ?: UUID.randomUUID().toString()


        btn.setOnClickListener{
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()
            val newTask = Task(id = id, title = title, description = description);
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent);
            finish()
        }

    }
}