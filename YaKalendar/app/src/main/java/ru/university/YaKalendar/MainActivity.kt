package ru.university.YaKalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter

    private val taskList = listOf(
        Task(1, "Купить хлеб", "Зайти в магазин", "2025-01-15"),
        Task(2, "Позвонить маме", "Обсудить выходные", "2025-01-16"),
        Task(3, "Сделать зарядку", "30 минут", "2025-01-17")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = AppDatabase.getInstance(this)
        val dao = db.taskDao()

        lifecycleScope.launch {
            val tasks = dao.getAllTasks()
            adapter = TaskAdapter(tasks) { task ->
                val intent = Intent(this@MainActivity, TaskDetailActivity::class.java)
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }

        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }
}