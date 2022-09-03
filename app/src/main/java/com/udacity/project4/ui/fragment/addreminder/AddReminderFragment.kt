package com.udacity.project4.ui.fragment.addreminder

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import com.example.project4.model.Reminder
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentAddReminderBinding
import com.udacity.project4.ui.fragment.addreminder.geofence.GeofenceHelper
import com.udacity.project4.ui.fragment.base.BaseFragment
import com.udacity.project4.ui.fragment.mainFragment.MainFragment.Companion.TAG
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddReminderFragment : BaseFragment() {
    override val _baseViewModel: AddReminderViewModel by viewModel()
    lateinit var binding: FragmentAddReminderBinding
    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var geofencingClient: GeofencingClient
    private var isFineLocationGenerated: Boolean = false
    private var isCoarseLocationGenerated: Boolean = false
    private var isBackgroundLocationGenerated: Boolean = false
    lateinit var reminder: Reminder
    private val onRequestLocationResultHandler = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == AppCompatActivity.RESULT_OK) {
            val lat = _baseViewModel.reminder.latitude
            val lng = _baseViewModel.reminder.longitude
            if (lat != 0.0 && lng != 0.0) {
                if (lat != null && lng != null) {
                    if (_baseViewModel.validateAndSaveReminder()) {
                        addGeofence(LatLng(lat, lng), 200f, _baseViewModel.reminder.id)
                    }
                }
            }
        } else {
            checkDeviceLocationSettingsAndStartGeofence(false)
            _baseViewModel.showToast.value = "Location on fail"
//            Toast.makeText(activity, "Location on fail", Toast.LENGTH_SHORT).show();
            Log.d("dapgoo", "Location off")
        }
    }


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions?.apply {
            isFineLocationGenerated = permissions[Manifest.permission.ACCESS_FINE_LOCATION]!!
            isCoarseLocationGenerated =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION]!!
            if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.Q
            ) {
                isBackgroundLocationGenerated =
                    permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION]!!
            }
        }
        if (isCoarseLocationGenerated && isFineLocationGenerated && isBackgroundLocationGenerated) {
            Log.d("dapgoo", "permissions on")
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            Log.d("dapgoo", "permissions off")
            _baseViewModel.showToast.value = "permissions fail"
//            Toast.makeText(activity, "permissions fail", Toast.LENGTH_SHORT).show();
            requestForegroundAndBackgroundLocationPermissions()

        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddReminderBinding.inflate(layoutInflater, container, false)
        binding.viewModel = _baseViewModel
        _baseViewModel.eventGoToMap.observe(viewLifecycleOwner) {
            if (it) {
                navigateToMap()
                _baseViewModel.completeGoToMapDestination()
            }
        }
        _baseViewModel.eventGoToList.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.reminderFragment)
                _baseViewModel.completeGoToList()
            }
        }
        binding.saveReminder.setOnClickListener {
            checkPermissionsAndStartGeofencing()
        }
        binding.selectLocation.setOnClickListener {
            _baseViewModel.goToMapDestination()
        }
//        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.add_reminder)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.reminderTitle.isEnabled = false
        binding.reminderDescription.isEnabled = false
        geofencingClient = LocationServices.getGeofencingClient(requireContext())
        geofenceHelper = GeofenceHelper(context)
        initMenu()

    }


    private fun checkPermissionsAndStartGeofencing() {

        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_LOW_POWER
        }
        val requestBuilder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(requestBuilder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(exception.resolution).build()
                    onRequestLocationResultHandler.launch(intentSenderRequest)

                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(requireContext(), sendEx.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(
                    requireView(),
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }

        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                val lat = _baseViewModel.reminder.latitude
                val lng = _baseViewModel.reminder.longitude
                if (lat != 0.0 && lng != 0.0) {
                    if (lat != null && lng != null) {
                        val result = _baseViewModel.validateAndSaveReminder()
                        if (result) {
                            Log.d("dapgoo", "reminder_saved")
                            _baseViewModel.showSnackBar.value = getString(R.string.reminder_saved)
                            addGeofence(LatLng(lat, lng), 200f, _baseViewModel.reminder.id)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(
        latLng: LatLng,
        radius: Float,
        geofenceId: String
    ) {
        val geofence: Geofence = geofenceHelper.getGeofence(
            geofenceId,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest: GeofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent? = geofenceHelper.getGeofencePendingIntent()
        pendingIntent?.let {

            geofencingClient.addGeofences(geofencingRequest, it)
                .addOnSuccessListener(OnSuccessListener {
//                    Toast.makeText(context, "geofence added", Toast.LENGTH_LONG).show()
                    _baseViewModel.showToast.value = getString(R.string.geofence_added)
                    Log.d("dapgoo", "Geofence Added")
                })
                .addOnFailureListener(OnFailureListener { e ->
                    val errorMessage: String? = geofenceHelper.getErrorString(e)
                    Toast.makeText(
                        context,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("dapgoo", "Geofence Fails $errorMessage")

                })
        }
    }

    private fun navigateToMap() {
        if (_baseViewModel.reminder.latitude != 0.0 && _baseViewModel.reminder.longitude != 0.0) {
            val action = AddReminderFragmentDirections.actionAddReminderFragmentToMapsFragment()
            action.isSelectLocation = true
            action.latitude = _baseViewModel.reminder.latitude!!.toFloat()
            action.longitude = _baseViewModel.reminder.longitude!!.toFloat()
            findNavController().navigate(action)
        } else {
            findNavController().navigate(R.id.mapsFragment)

        }
    }

    override fun onResume() {
        super.onResume()
        holdData()
    }

    private fun holdData() {
        arguments?.let {
            val args = AddReminderFragmentArgs.fromBundle(it)
            args.apply {
                if (isAddLocation) {
                    updateLocation()
                    _baseViewModel.reminder.latitude = latitude.toDouble()
                    _baseViewModel.reminder.longitude = longitude.toDouble()
                    if (location == "") {
                        _baseViewModel.reminder.location = "المكان غير معرف"
                    } else {
                        _baseViewModel.reminder.location = location
                    }

                }
            }


        }

    }

    private fun updateLocation() {
        binding.tvSelectedLocation.visibility = View.GONE
        binding.reminderTitle.isEnabled = true
        binding.reminderDescription.isEnabled = true
        binding.reminderTitle.hint = getString(R.string.reminder_title)
        binding.reminderDescription.hint = getString(R.string.reminder_desc)


    }

    private fun initMenu() {
        val host = requireActivity()
        host.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {


                return true
            }
        })
    }


    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        var backgroundPermissionApproved = false
        val foregroundFineLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))
        val foregroundCoarseLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ))
        return foregroundFineLocationApproved && foregroundCoarseLocationApproved && backgroundPermissionApproved
    }

    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return
        var permissionsArray = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.Q
        ) {
            permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
            locationPermissionRequest.launch(permissionsArray)
        } else {
            locationPermissionRequest.launch(permissionsArray)
        }

    }

}