package ru.university.YaKalendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database"
                )
                    .addCallback(SeedCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Синхронные SQL-вставки в onCreate — надёжный и простой способ.
        // Никаких корутин и getInstance() внутри — только голый SQL.
        private val SeedCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(
                    "INSERT INTO tasks (title, description, date, time, isDone) VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any?>("Купить хлеб", "Зайти в магазин", "2025-01-15", "18:00", 0)
                )
                db.execSQL(
                    "INSERT INTO tasks (title, description, date, time, isDone) VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any?>("Позвонить маме", "Обсудить выходные", "2025-01-16", "12:00", 0)
                )
                db.execSQL(
                    "INSERT INTO tasks (title, description, date, time, isDone) VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any?>("Сделать зарядку", "30 минут", "2025-01-17", "08:00", 0)
                )
            }
        }
    }
}