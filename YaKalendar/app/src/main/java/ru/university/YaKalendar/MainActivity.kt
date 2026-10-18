package ru.university.YaKalendar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private var seedConfirmed = false

    private val viewModel: TaskViewModel by viewModels {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = TaskRepository(db.taskDao())
        TaskViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter { task ->
            val intent = Intent(this@MainActivity, TaskDetailActivity::class.java)
            intent.putExtra("task_id", task.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Подписка на StateFlow из ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    adapter.submitList(tasks)

                    // Однократное подтверждение, что сид-данные загрузились.
                    // (Показывается, только если в базе уже есть задачи.)
                    if (!seedConfirmed && tasks.isNotEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "База загружена: ${tasks.size} задач(и)",
                            Toast.LENGTH_SHORT
                        ).show()
                        seedConfirmed = true
                    }
                }
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)

        // Короткое нажатие — переход к экрану создания задачи (как было)
        fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        // Длинное нажатие — тестовая вставка через ViewModel + подтверждение
        fab.setOnLongClickListener {
            viewModel.addTestTask()
            Toast.makeText(
                this@MainActivity,
                "Тестовая задача добавлена",
                Toast.LENGTH_SHORT
            ).show()
            true
        }
    }
}