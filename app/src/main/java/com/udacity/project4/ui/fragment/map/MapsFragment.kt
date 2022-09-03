package com.udacity.project4.ui.fragment.map

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentMapsBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment() {
    private lateinit var map: GoogleMap

    private var lat: Double = 0.0
    private var long: Double = 0.0
    private lateinit var location: String
    private lateinit var binding: FragmentMapsBinding
    val viewModel: MapViewModel by viewModel()


    private val locationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("mApp", "isGranted")
                map.isMyLocationEnabled = true
            } else {
                Log.d("mApp", "Permission denied")
                Snackbar.make(
                    requireView(),
                    "Permission denied..... you need foreground permission to access your location on map",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    requestPermission()

                }.show()
            }
        }


    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        enableUserLocation()
        setMapStyle(map)
        setPoiClick(map)
        setMapLongClick(map)
        viewModel.stateOfLocation.observe(viewLifecycleOwner)
        {
            it?.let {
                moveCameraToLocation(latLng = it)
            }
        }


    }

    private fun enableUserLocation() {

        when {
            (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) -> {
                // You can use the API that requires the permission.
                map.isMyLocationEnabled = true


                Toast.makeText(context, "Location permission is granted.", Toast.LENGTH_LONG).show()
            }
            (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )) -> {
                // Explain why you need the permission
                // Add dialog
                Toast.makeText(
                    context,
                    "Location permission important to access  your location",
                    Toast.LENGTH_LONG
                ).show()
                requestPermission()
            }

            else ->
                //Request permission
                requestPermission()

        }

    }



    private fun requestPermission() {
        locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        viewModel.eventGoToAddReminder.observe(viewLifecycleOwner) {
            if (it) {
                navigateToAddDestination()
                viewModel.completeGoToAddReminderDestination()
            }
        }

        initMenu()


        binding.saveLocation.setOnClickListener {
            viewModel.goToAddReminderDestination()
        }

        holdData()
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_fragment_maps)


        return binding.root
    }


    private fun holdData() {
        arguments?.let {
            val args = MapsFragmentArgs.fromBundle(it)
            args.apply {
                if (isSelectLocation) {
                    viewModel.setLocation(LatLng(latitude.toDouble(), longitude.toDouble()))
                }
            }


        }

    }

    private fun moveCameraToLocation(latLng: LatLng) {
        map.addMarker(
            MarkerOptions()
                .position(latLng)
        )
        val zoomLevel = 16f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
        map.addCircle(drawCircleRadius(latLng))
        if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
            lat = latLng.latitude
            long = latLng.longitude
        }
    }

    private fun navigateToAddDestination() {
        if (lat != 0.0 && long != 0.0) {
            val action =
                MapsFragmentDirections.actionMapsFragmentToAddReminderFragment()
            action.isAddLocation = true
            action.location = location
            action.longitude = long.toFloat()
            action.latitude = lat.toFloat()
            findNavController().navigate(action)


        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        initMenu()

    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
            )
            lat = latLng.latitude
            long = latLng.longitude
            location = ""

        }
    }

    private fun drawCircleRadius(latLng: LatLng): CircleOptions {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)

        circleOptions.radius(30.0)
        circleOptions.fillColor(Color.TRANSPARENT)
        circleOptions.strokeWidth(6f)
        return circleOptions
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            val zoomLevel = 16f
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi.latLng, zoomLevel))
            poiMarker?.showInfoWindow()
            lat = poi.latLng.latitude
            long = poi.latLng.longitude
            location = poi.name
            map.addCircle(drawCircleRadius(poi.latLng))
        }


    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Toast.makeText(context, "Style parsing failed.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(context, "error $e", Toast.LENGTH_LONG).show()
        }
    }

    private fun initMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.types_map_menu, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.normal_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_NORMAL
                        return false
                    }
                    R.id.satellite_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_SATELLITE

                        return true
                    }
                    R.id.hybrid_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_HYBRID
                        return true
                    }
                    R.id.terrain_map -> {
                        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        return true
                    }

                }
                return false

            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


}