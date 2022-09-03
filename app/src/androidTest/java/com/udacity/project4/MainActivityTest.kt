package com.udacity.project4

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.project4.repository.ReminderLocalRepository
import com.udacity.project4.data.source.ReminderDataSource
import com.udacity.project4.data.source.loacal.room.ReminderDatabase
import com.udacity.project4.ui.activity.MainActivity
import com.udacity.project4.ui.fragment.addreminder.AddReminderViewModel
import com.udacity.project4.ui.fragment.reminder.RemindersViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.EspressoIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
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

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class MainActivityTest : KoinTest {// Extended Koin Test - embed autoclose @after method to close Koin after every test

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
                    getApplicationContext(),
                    get() as ReminderDataSource
                )
            }


            single {
                AddReminderViewModel(
                    getApplicationContext(),
                    get() as ReminderDataSource
                )
            }

            single<ReminderDataSource> { ReminderLocalRepository(get()) }
            single { ReminderDatabase.createDao(getApplicationContext()) }
        }

        //declare a new koin module
        startKoin {
            androidContext(getApplicationContext())
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
    fun launchMainActivity() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)
    }


    @Test
    fun checkVisibilityOfContainer() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)
        onView(withId(R.id.mainFragment)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}
