package com.udacity.project4.ui.fragment.addreminder.geofence

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.project4.model.Reminder
import com.example.project4.repository.ReminderLocalRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.data.source.ReminderDataSource
import com.udacity.project4.data.source.Result
import kotlinx.coroutines.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    private val TAG = "GeofenceService"
    private lateinit var repository: ReminderDataSource
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob


    companion object {
        private const val JOB_ID = 573

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }

    }

    override fun onHandleWork(intent: Intent) {


        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        val geofenceList: List<Geofence> =
            geofencingEvent!!.triggeringGeofences!!
        for (i in geofenceList) {
            if (!geofenceList.contains(i)) {
                sendNotification(i)
            }
        }
    }

    private fun sendNotification(geofence: Geofence) {
        val requestId = geofence.requestId

        if (TextUtils.isEmpty(requestId)) return

        repository = get()
        val remindersLocalRepository: ReminderLocalRepository by inject()
        CoroutineScope(coroutineContext).launch(SupervisorJob())
        {
            val result = remindersLocalRepository.getReminder(requestId)
            if (result is Result.Success<Reminder>) {
                val reminder = result.data
                reminder?.apply {
                    com.udacity.project4.util.sendNotification(
                        this@GeofenceTransitionsJobIntentService, this
                    )
                }

            }
        }
    }


}