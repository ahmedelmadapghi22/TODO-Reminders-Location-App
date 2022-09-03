package com.udacity.project4.ui.fragment.reminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.example.project4.model.Reminder
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.data.source.Result
import com.udacity.project4.getString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    val list = listOf<Reminder>(
        Reminder("title", "description", "location", 0.0, 0.0),
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
    private val Reminder1 = list[0]
    private val Reminder2 = list[1]
    private val Reminder3 = list[2]

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var ReminderListViewModel: RemindersViewModel

    @After
    fun tearDown() {
        stopKoin()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getRemindersList() {
        runTest {
            val RemindersList = mutableListOf(Reminder1, Reminder2, Reminder3)
            fakeDataSource = FakeDataSource(RemindersList)
            ReminderListViewModel =
                RemindersViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
            ReminderListViewModel.getReminders()

            Assert.assertThat(
                ReminderListViewModel.reminders.getOrAwaitValue(),
                (IsNot.not(emptyList()))
            )
            Assert.assertThat(
                ReminderListViewModel.reminders.getOrAwaitValue().size,
                `is`(RemindersList.size)
            )
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun check_loading() {
        fakeDataSource = FakeDataSource(mutableListOf())
        ReminderListViewModel =
            RemindersViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        ReminderListViewModel.getReminders()
        Assert.assertThat(
            ReminderListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
        mainCoroutineRule.resumeDispatcher()
        Assert.assertThat(
            ReminderListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )


    }


    @ExperimentalCoroutinesApi
    @Test
    fun returnError() {
        runTest {
            fakeDataSource = FakeDataSource(null)
            ReminderListViewModel =
                RemindersViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
            ReminderListViewModel.getReminders()
            Assert.assertEquals(
                ReminderListViewModel.showSnackBar.getOrAwaitValue(),
                "No reminders found"
            )
        }


    }

    @ExperimentalCoroutinesApi
    @Test
    fun remindersUnavailable_showsError() = runBlockingTest {
        fakeDataSource = FakeDataSource(null)
        fakeDataSource.setShouldReturnError(true)
        ReminderListViewModel =
            RemindersViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        ReminderListViewModel.getReminders()
        Assert.assertEquals(ReminderListViewModel.showSnackBar.getOrAwaitValue(), "Error occurred")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun reminderUnavailable_showsError() = runBlockingTest {
        fakeDataSource = FakeDataSource(null)
        fakeDataSource.setShouldReturnError(true)
        ReminderListViewModel =
            RemindersViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        Assert.assertEquals(ReminderListViewModel.showSnackBar.getOrAwaitValue(), "Error occurred")
    }
}
