package com.udacity.project4.data.source.loacal.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project4.model.Reminder

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDoa

    companion object{
        fun createDao(context: Context): ReminderDoa {
            return Room.databaseBuilder(
                context.applicationContext,
                ReminderDatabase::class.java,
                "reminders_table"
            ).build().reminderDao()
        }

    }



}
