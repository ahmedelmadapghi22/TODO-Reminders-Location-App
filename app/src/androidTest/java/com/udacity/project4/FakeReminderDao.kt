package com.example.project4

import com.udacity.project4.data.source.loacal.room.ReminderDoa
import com.example.project4.model.Reminder


class FakeReminderDao : ReminderDoa {

    var shouldReturnError = false

    public val remindersServiceData: LinkedHashMap<String, Reminder> = LinkedHashMap()

    override suspend fun getReminders(): List<Reminder> {
        if (shouldReturnError) {
            throw (Exception("Test exception"))
        }

        val list = mutableListOf<Reminder>()
        list.addAll(remindersServiceData.values)
        return list
    }

    override suspend fun getReminderById(reminderId: String): Reminder? {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        remindersServiceData[reminderId]?.let {
            return it
        }
        return null
    }

    override suspend fun saveReminder(reminder: Reminder) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()

    }


}