package com.udacity.project4.data.source

import com.example.project4.model.Reminder

interface ReminderDataSource {
    suspend fun getReminders(): Result<List<Reminder>>
    suspend fun saveReminder(reminder: Reminder)
    suspend fun getReminder(id: String): Result<Reminder>
    suspend fun deleteAllReminders()

}