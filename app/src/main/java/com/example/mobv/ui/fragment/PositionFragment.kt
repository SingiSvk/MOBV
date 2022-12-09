package com.example.mobv.ui.fragment

import android.Manifest
import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.mobv.GeofenceBroadcastReceiver
import com.example.mobv.R
import com.example.mobv.databinding.FragmentPositionBinding
import com.example.mobv.helpers.Injection
import com.example.mobv.helpers.PreferenceData
import com.example.mobv.ui.viewmodel.MapViewModel
import com.example.mobv.ui.viewmodel.data.MyLocation
import com.example.mobv.ui.viewmodel.data.NearbyPub
import com.example.mobv.ui.widget.nearbyPubs.NearbyPubsEvents
import com.google.android.gms.location.*

class PositionFragment : Fragment() {
    private lateinit var binding: FragmentPositionBinding
    private lateinit var viewModel: MapViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                // Precise location access granted.
            }
            else -> {
                viewModel.show("Background location access denied.")
                // No location access granted.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            Injection.providePubsViewModelFactory(requireContext())
        )[MapViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPositionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_to_login)
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.back.setOnClickListener {
                it.findNavController().popBackStack()
            }
            bnd.swipeRefresh.setOnRefreshListener {
                loadData()
            }

            val lottieFavButton: LottieAnimationView = binding.checkInBtn

            lottieFavButton.setMaxProgress(0.38f)
            lottieFavButton.resumeAnimation()

            bnd.checkInBtn.setOnClickListener {
                lottieFavButton.setMaxProgress(1f)
                lottieFavButton.resumeAnimation()
                lottieFavButton.addAnimatorListener(object : AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {
                    }

                    // Najprv animácia, potom check in, aby prebehla celá animácia
                    // Ak chceme check in hneď, tak v onAnimationStart
                    override fun onAnimationEnd(animation: Animator) {
                        if (checkBackgroundPermissions()) {
                            viewModel.checkIn()
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                permissionDialog()
                            }
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                })
            }

            bnd.nearbyPubs.events = object : NearbyPubsEvents {
                override fun onPubClick(nearbyPub: NearbyPub) {
                    viewModel.myPub.postValue(nearbyPub)
                }
            }
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it
        }
        viewModel.checkedIn.observe(viewLifecycleOwner) { it ->
            it?.getContentIfNotHandled()?.let { it ->
                if (it) {
                    viewModel.show("Successfully checked in.")
                    viewModel.myLocation.value?.let {
                        createFence(it.lat, it.lon)
                    }
                }
            }
        }

        if (checkPermissions()) {
            loadData()
        } else {
            Navigation.findNavController(requireView()).navigate(R.id.action_to_pubs)
        }

        viewModel.message.observe(viewLifecycleOwner) {
            if (PreferenceData.getInstance().getUserItem(requireContext()) == null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_to_login)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadData() {
        if (checkPermissions()) {
            viewModel.loading.postValue(true)
            fusedLocationClient.getCurrentLocation(
                CurrentLocationRequest.Builder().setDurationMillis(30000)
                    .setMaxUpdateAgeMillis(60000).build(), null
            ).addOnSuccessListener {
                it?.let {
                    viewModel.myLocation.postValue(MyLocation(it.latitude, it.longitude))
                } ?: viewModel.loading.postValue(false)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun createFence(lat: Double, lon: Double) {
        if (!checkPermissions()) {
            viewModel.show("Geofence failed, permissions not granted.")
        }
        val geofenceIntent = PendingIntent.getBroadcast(
            requireContext(), 0,
            Intent(requireContext(), GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val request = GeofencingRequest.Builder().apply {
            addGeofence(
                Geofence.Builder()
                    .setRequestId("mygeofence")
                    .setCircularRegion(lat, lon, 300F)
                    .setExpirationDuration(1000L * 60 * 60 * 24)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()
            )
        }.build()

        geofencingClient.addGeofences(request, geofenceIntent).run {
            addOnSuccessListener {
                Navigation.findNavController(requireView()).navigate(R.id.action_to_pubs)
            }
            addOnFailureListener {
                viewModel.show("Geofence failed to create.") //permission is not granted for All times.
                it.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun permissionDialog() {
        val alertDialog: AlertDialog = requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Background location needed")
                setMessage("Allow background location (All times) for detecting when you leave pub.")
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            )
                        )
                    })
                setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            }
            // Create the AlertDialog
            builder.create()
        }
        alertDialog.show()
    }

    private fun checkBackgroundPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}