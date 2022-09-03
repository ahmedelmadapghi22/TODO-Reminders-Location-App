package com.udacity.project4.ui.fragment.addreminder.geofence


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.ui.fragment.addreminder.geofence.GeofenceTransitionsJobIntentService.Companion.enqueueWork


class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "GeofenceBroadcast"
    override fun onReceive(context: Context, intent: Intent) {
        val geofenceEvent = GeofencingEvent.fromIntent(intent)
        geofenceEvent?.apply {
            if (hasError()) {
                Log.d(TAG, "Error on receive !")
                return
            }
        }

        when (geofenceEvent?.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                enqueueWork(context, intent)
            }
        }

    }
}