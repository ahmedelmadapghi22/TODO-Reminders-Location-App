package com.example.project4

import android.app.Application
import com.udacity.project4.data.source.ReminderDataSource
import com.udacity.project4.data.source.loacal.room.ReminderDatabase
import com.example.project4.repository.ReminderLocalRepository
import com.udacity.project4.ui.fragment.addreminder.AddReminderViewModel
import com.udacity.project4.ui.fragment.map.MapViewModel
import com.udacity.project4.ui.fragment.reminder.RemindersViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ReminderApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        val myModule = module {
            viewModel {
                RemindersViewModel(
                    get(),
                    get()
                )
            }
            viewModel {
                AddReminderViewModel(
                    get(),
                    get()
                )
            }
            viewModel {
                MapViewModel()
            }
            // RemindersLocalRepository
            single<ReminderLocalRepository> {
                ReminderLocalRepository(get())
            }
            // ReminderDataSource
            single<ReminderDataSource> {
                get<ReminderLocalRepository>()
            }
            single { ReminderDatabase.createDao(this@ReminderApplication) }

        }
        startKoin {
            androidContext(this@ReminderApplication)
            modules(listOf(myModule))
        }
    }


}