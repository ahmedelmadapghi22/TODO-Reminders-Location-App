package com.udacity.project4.util

import com.udacity.project4.data.source.ReminderDataSource
import com.udacity.project4.data.source.Result
import com.example.project4.model.Reminder

//Use FakeDataSource that acts as a test double to the LocalDataSource

class FakeDataSource(var reminders: MutableList<Reminder>? = mutableListOf()) :
    ReminderDataSource {

    //    DONE: Create a fake data source to act as a double to the real data source
    private var shouldReturnError = false
    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }

    override suspend fun getReminders(): Result<List<Reminder>> {
        return if (shouldReturnError) {
            Result.Error("Error occurred")
        } else {

            reminders?.let { return Result.Success(it) }
            return Result.Error("No reminders found")
        }
    }

    override suspend fun saveReminder(reminder: Reminder) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<Reminder> {
        return if (shouldReturnError) {
            Result.Error("Error occurred")
        } else {
            reminders?.firstOrNull { it.id == id }?.let { return Result.Success(it) }
            return Result.Error("Reminder not found")
        }

    }

    override suspend fun deleteAllReminders() {
        reminders = mutableListOf()
    }

}