package com.udacity.project4.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.project4.model.Reminder
import com.example.project4.repository.ReminderLocalRepository
import com.udacity.project4.R
import com.udacity.project4.data.source.ReminderDataSource
import com.udacity.project4.data.source.loacal.room.ReminderDatabase
import com.udacity.project4.ui.activity.MainActivity
import com.udacity.project4.ui.fragment.addreminder.AddReminderFragment
import com.udacity.project4.ui.fragment.addreminder.AddReminderViewModel
import com.udacity.project4.util.*
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito.mock


@RunWith(AndroidJUnit4::class)
@MediumTest
class AddFragmentTest : KoinTest {


    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var viewModel: AddReminderViewModel


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
        stopKoin()

        val appModule = module {
            single {
                AddReminderViewModel(
                    ApplicationProvider.getApplicationContext(),
                    get() as ReminderDataSource
                )
            }

            single<ReminderDataSource> { ReminderLocalRepository(get()) }
            single { ReminderDatabase.createDao(ApplicationProvider.getApplicationContext()) }
        }

        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(appModule))
        }

        viewModel = AddReminderViewModel(get(), get())

        viewModel.set(
            Reminder(
                "title",
                "description",
                "location",
                (-360..360).random().toDouble(),
                (-360..360).random().toDouble()
            )
        )

    }


    @Test
    fun errData_noTitleWillFail() {
        val navController = mock(NavController::class.java)
        val scenario =
            launchFragmentInContainer<AddReminderFragment>(Bundle.EMPTY, R.style.AppTheme)
        scenario.withFragment {
            this._baseViewModel.reminder.location = "location"
            this._baseViewModel.reminder.title = null
            this._baseViewModel.reminder.description = "description"
            this._baseViewModel.reminder.longitude = (-360..360).random().toDouble()
            this._baseViewModel.reminder.latitude = (-360..360).random().toDouble()

        }
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        dataBindingIdlingResource.monitorFragment(scenario)


        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText(getString(R.string.err_enter_title)))
            .check(
                matches(
                    withEffectiveVisibility(
                        Visibility.VISIBLE
                    )
                )
            )


    }

    @Test
    fun errData_noLocationWillFail() {
        val navController = mock(NavController::class.java)
        val scenario =
            launchFragmentInContainer<AddReminderFragment>(Bundle.EMPTY, R.style.AppTheme)
        scenario.withFragment {
            this._baseViewModel.reminder.location = null
            this._baseViewModel.reminder.title = "title"
            this._baseViewModel.reminder.description = "description"
            this._baseViewModel.reminder.longitude = (-360..360).random().toDouble()
            this._baseViewModel.reminder.latitude = (-360..360).random().toDouble()

        }
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        dataBindingIdlingResource.monitorFragment(scenario)
        onView(withId(R.id.saveReminder)).perform(click())
        closeSoftKeyboard()

        onView(withText(getString(R.string.error_select_location)))
            .check(
                matches(
                    withEffectiveVisibility(
                        Visibility.VISIBLE
                    )
                )
            )

        closeSoftKeyboard()


    }

    @Test
    fun successData_ToastSave() {
        val navController = mock(NavController::class.java)
        val scenario =
            launchFragmentInContainer<AddReminderFragment>(Bundle.EMPTY, R.style.AppTheme)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        scenario.withFragment {
            this._baseViewModel.reminder.location = "location"
            this._baseViewModel.reminder.title = "title"
            this._baseViewModel.reminder.description = "description"
            this._baseViewModel.reminder.longitude = (-360..360).random().toDouble()
            this._baseViewModel.reminder.latitude = (-360..360).random().toDouble()

        }
        dataBindingIdlingResource.monitorFragment(scenario)
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText(getString(R.string.geofence_added))).inRoot(ToastMatcher())
            .check(matches(isDisplayed()))


    }


}

