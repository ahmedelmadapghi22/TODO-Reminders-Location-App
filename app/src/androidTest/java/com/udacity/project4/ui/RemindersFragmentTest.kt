package com.udacity.project4.ui

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.project4.model.Reminder
import com.example.project4.repository.ReminderLocalRepository
import com.udacity.project4.R
import com.udacity.project4.data.source.ReminderDataSource
import com.udacity.project4.data.source.loacal.room.ReminderDatabase
import com.udacity.project4.ui.fragment.addreminder.AddReminderViewModel
import com.udacity.project4.ui.fragment.reminder.RemindersFragment
import com.udacity.project4.ui.fragment.reminder.RemindersViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.EspressoIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersFragmentTest : KoinTest {

    private lateinit var datasoure: ReminderDataSource

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource(): Unit = IdlingRegistry.getInstance().run {
        register(EspressoIdlingResource.countingIdlingResource)
        register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource(): Unit = IdlingRegistry.getInstance().run {
        unregister(EspressoIdlingResource.countingIdlingResource)
        unregister(dataBindingIdlingResource)
        stopKoin()
    }

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        val myModule = module {
            viewModel {
                RemindersViewModel(
                    ApplicationProvider.getApplicationContext(),
                    get() as ReminderDataSource
                )
            }


            single {
                AddReminderViewModel(
                    ApplicationProvider.getApplicationContext(),
                    get() as ReminderDataSource
                )
            }

            single<ReminderDataSource> { ReminderLocalRepository(get()) }
            single { ReminderDatabase.createDao(ApplicationProvider.getApplicationContext()) }
        }

        //declare a new koin module
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(myModule))
        }

        //Get our real repository
        datasoure = get()

        //clear the data to start fresh

        runBlocking {
            datasoure.deleteAllReminders()
        }
    }


    @Test
    fun clickOnAddFab_navigatesToAddReminderFragment() {
        val scenario =
            launchFragmentInContainer<RemindersFragment>(Bundle.EMPTY, R.style.AppTheme)
        val navController = mock(NavController::class.java)
        dataBindingIdlingResource.monitorFragment(scenario)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.navigate_btn)).perform(click())
        verify(navController).navigate(R.id.addReminderFragment)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun withoutReminders_showsNoData() {
        val scenario =
            launchFragmentInContainer<RemindersFragment>(Bundle.EMPTY, R.style.AppTheme)

        val navController = mock(NavController::class.java)

        dataBindingIdlingResource.monitorFragment(scenario)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        onView(withId(R.id.rv_reminder)).check(matches(not(isDisplayed())));


    }

    @ExperimentalCoroutinesApi
    @Test
    fun withReminders_showsOnScreen() {
        val reminder = Reminder(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        )

        runTest {
            datasoure.saveReminder(reminder)
        }

        val scenario =
            launchFragmentInContainer<RemindersFragment>(Bundle.EMPTY, R.style.AppTheme)
        val navController = mock(NavController::class.java)
        dataBindingIdlingResource.monitorFragment(scenario)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.iv_no_data)).check(matches(not(isDisplayed())));
        onView(withId(R.id.tv_no_data)).check(matches(not(isDisplayed())));
    }


}