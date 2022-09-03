package com.udacity.project4.data.source.loacal.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.project4.model.Reminder

@Dao
interface ReminderDoa {
    @Query("SELECT * FROM location_reminders")
    suspend fun getReminders(): List<Reminder>


    @Query("SELECT * FROM location_reminders where entry_id = :reminderId")
    suspend fun getReminderById(reminderId: String): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminder(reminder: Reminder)

    @Query("DELETE  FROM location_reminders")
    suspend fun deleteAllReminders()


}