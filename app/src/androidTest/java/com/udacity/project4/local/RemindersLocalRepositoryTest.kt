package com.udacity.project4.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.project4.FakeReminderDao
import com.udacity.project4.data.source.Result
import com.example.project4.model.Reminder
import com.example.project4.repository.ReminderLocalRepository
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.data.source.loacal.room.ReminderDatabase
import com.udacity.project4.data.source.loacal.room.ReminderDoa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

//    DONE: Add testing implementation to the RemindersLocalRepository.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    val list = listOf<Reminder>(
        Reminder(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        Reminder(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        Reminder(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        Reminder(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        )
    )

    private val reminder1 = list[0]
    private val reminder2 = list[1]
    private val reminder3 = list[2]

    private val newReminder = list[3]

    private lateinit var database: ReminderDatabase
    private lateinit var remindersLocalRepository: ReminderLocalRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ReminderDatabase::class.java
        ).allowMainThreadQueries().build()
        remindersLocalRepository = ReminderLocalRepository(
            database.reminderDao(), Dispatchers.Unconfined
        )
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun savesToLocalCache() {
        runTest {
            remindersLocalRepository.saveReminder(newReminder)

            val res: Result<List<Reminder>> = remindersLocalRepository.getReminders()
            assertThat(res).isInstanceOf(Result.Success::class.java)
            res as Result.Success

            assertThat(res.data).isNotEmpty()
            assertThat(res.data).hasSize(1)

        }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun getReminderByIdThatExistsInLocalCache() {
        runTest {
            // Make sure newReminder is not in the local cache
            assertThat((remindersLocalRepository.getReminder(reminder1.id) as? Result.Error)?.message).isEqualTo(
                "Reminder not found!"
            )

//            realRemindersDao.remindersServiceData[reminder1.id] = reminder1

            // When reminder is fetch by id
            remindersLocalRepository.saveReminder(reminder1)
            val loadedReminder =
                (remindersLocalRepository.getReminder(reminder1.id) as? Result.Success)?.data

            Assert.assertThat<Reminder>(loadedReminder as Reminder, CoreMatchers.notNullValue())
            Assert.assertThat(loadedReminder.id, CoreMatchers.`is`(reminder1.id))
            Assert.assertThat(loadedReminder.title, CoreMatchers.`is`(reminder1.title))
            Assert.assertThat(loadedReminder.description, CoreMatchers.`is`(reminder1.description))
            Assert.assertThat(loadedReminder.location, CoreMatchers.`is`(reminder1.location))
            Assert.assertThat(loadedReminder.latitude, CoreMatchers.`is`(reminder1.latitude))
            Assert.assertThat(loadedReminder.longitude, CoreMatchers.`is`(reminder1.longitude))
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getReminderByIdThatDoesNotExistInLocalCache() {
        runTest {

            val message =
                (remindersLocalRepository.getReminder(reminder1.id) as? Result.Error)?.message
            assertThat(message).isEqualTo("Reminder not found!")

        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAllReminders_EmptyListFetchedFromLocalCache() {
        runTest {
            assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isEmpty()

        }
    }
}