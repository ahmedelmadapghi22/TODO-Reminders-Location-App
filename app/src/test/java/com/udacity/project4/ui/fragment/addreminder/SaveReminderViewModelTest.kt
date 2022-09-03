package com.udacity.project4.ui.fragment.addreminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.data.FakeDataSource
import com.example.project4.model.Reminder
import com.udacity.project4.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: AddReminderViewModel
    private lateinit var application: Application

    @get:Rule
    val instantTaskExecRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    private val firstReminder = Reminder(
        null,
        "description",
        "location",
        (-720..360).random().toDouble(),
        (-360..360).random().toDouble()
    )
    private val secondReminder = Reminder(
        "title",
        "description",
        "location",
        (-720..360).random().toDouble(),
        (-360..360).random().toDouble()
    )

    @Before
    fun setup() {
        stopKoin()
        application = ApplicationProvider.getApplicationContext()
        fakeDataSource = FakeDataSource()
        viewModel = AddReminderViewModel(
            application,
            fakeDataSource
        )
        viewModel.validateAndSaveReminder()
        viewModel.reminder = secondReminder

    }


    @ExperimentalCoroutinesApi
    @Test
    fun viewModel_showsLoadingAtStartAndHidesOnceDone() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.reminder = secondReminder
        viewModel.validateAndSaveReminder()
        Assert.assertEquals(viewModel.showLoading.value, true)
        mainCoroutineRule.resumeDispatcher()
        Assert.assertEquals(viewModel.showLoading.value, false)


    }

    @Test
    fun viewModel_savingValidItemSucceeds() {
        viewModel.reminder = secondReminder
        val returnValue = viewModel.validateAndSaveReminder()
        Assert.assertEquals(returnValue, true)
    }

    @Test
    fun viewModel_savingDataItemWithNullWillFail() {
        viewModel = AddReminderViewModel(
            application,
            fakeDataSource
        )
        viewModel.validateAndSaveReminder()

        val returnValue = viewModel.validateAndSaveReminder(

        )
        Assert.assertEquals(returnValue, false)
    }


}