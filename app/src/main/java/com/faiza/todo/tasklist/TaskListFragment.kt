package com.example.todoFaizaAkabli.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.todoFaizaAkabli.databinding.FragmentTaskListBinding
import com.example.todoFaizaAkabli.form.FormActivity
import com.example.todoFaizaAkabli.network.Api
import com.faiza.todo.user.UserInfoActivity
import com.faiza.todo.tasklist.Task
import com.faiza.todo.tasklist.TaskListAdapter
import com.faiza.todo.tasklist.TasksListViewModel
import kotlinx.coroutines.launch
import java.util.*


class TaskListFragment : Fragment() {
    val adapter = TaskListAdapter()
    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )
    private val viewModel: TasksListViewModel by viewModels()

    val createTask =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // ici on récupérera le résultat pour le traiter
            val task = result.data?.getSerializableExtra("task") as? Task
                ?: return@registerForActivityResult
            lifecycleScope.launch {
                viewModel.create(task)

            }
            adapter.submitList(taskList)
        }
    val updateTask =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // ici on récupérera le résultat pour le traiter
            val task = result.data?.getSerializableExtra("task") as? Task
            if (task != null) {
                lifecycleScope.launch {
                    viewModel.update(task)

                }
                adapter.submitList(taskList)
            }
        }
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        adapter.submitList(taskList)
        // Instanciation d'un objet task avec des données préremplies:
        val btn = binding.floatingActionButton
        btn.setOnClickListener {

            //  val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
            //  taskList = taskList + newTask
            // adapter.submitList(taskList)

            val intent = Intent(context, FormActivity::class.java)
            createTask.launch(intent)

        }
        binding.avatarImageView.setOnClickListener {
            val intent = Intent(context, UserInfoActivity::class.java)
            startActivity(intent)
        }

        adapter.onClickDelete = { task ->
            // Supprimer la tâche
            lifecycleScope.launch {
                viewModel.create(task)

            }
            adapter.submitList(taskList)
        }

        adapter.onClickEdit = { task ->
            val intent = Intent(context, FormActivity::class.java)
            intent.putExtra("task", task)
            updateTask.launch(intent)
        }
        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est executée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
                taskList = newList
                adapter.submitList(taskList)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        // Ici on ne va pas gérer les cas d'erreur donc on force le crash avec "!!"
        lifecycleScope.launch {
            viewModel.refresh()
            val userInfo = Api.userWebService.getInfo().body()!!
            val userInfos = binding.textView
            userInfos.text = "${userInfo.firstName} ${userInfo.lastName}"

             binding.avatarImageView.load("https://goo.gl/gEgYUd") {
                 crossfade(true)
                 transformations(CircleCropTransformation())
             }
        }

    }




}