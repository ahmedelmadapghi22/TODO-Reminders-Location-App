package com.example.project4.repository

import com.udacity.project4.data.source.ReminderDataSource
import com.udacity.project4.data.source.Result
import com.udacity.project4.data.source.loacal.room.ReminderDoa
import com.example.project4.model.Reminder
import com.udacity.project4.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderLocalRepository(
    private val remindersDao: ReminderDoa,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<Reminder>> {
        wrapEspressoIdlingResource {
            return withContext(dispatcher) {
                try {
                    Result.Success(remindersDao.getReminders())
                } catch (ex: Exception) {
                    Result.Error(ex.message)
                }
            }

        }

    }

    override suspend fun saveReminder(reminder: Reminder) {
        wrapEspressoIdlingResource {
            return remindersDao.saveReminder(reminder)

        }
    }

    override suspend fun getReminder(id: String): Result<Reminder> {

        wrapEspressoIdlingResource {
            return withContext(dispatcher) {
                try {
                    val reminder = remindersDao.getReminderById(id)
                    if (reminder != null) {
                        return@withContext Result.Success(reminder)
                    } else {
                        return@withContext Result.Error("Reminder not found!")
                    }
                } catch (ex: Exception) {
                    Result.Error(ex.message)
                }
            }
        }

    }

    override suspend fun deleteAllReminders() {
        wrapEspressoIdlingResource {
            withContext(dispatcher) {
                remindersDao.deleteAllReminders()
            }
        }

    }


}