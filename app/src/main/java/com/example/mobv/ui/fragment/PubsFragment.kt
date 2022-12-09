package com.example.mobv.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.mobv.R
import com.example.mobv.databinding.FragmentPubsBinding
import com.example.mobv.helpers.Injection
import com.example.mobv.helpers.PreferenceData
import com.example.mobv.ui.viewmodel.PubsViewModel
import com.example.mobv.ui.viewmodel.data.MyLocation
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class PubsFragment : Fragment() {
    private lateinit var binding: FragmentPubsBinding
    private lateinit var viewModel: PubsViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Navigation.findNavController(requireView()).navigate(R.id.action_to_locate)
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                viewModel.show("Only approximate location access granted.")
                // Only approximate location access granted.
            }
            else -> {
                viewModel.show("Location access denied.")
                // No location access granted.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel = ViewModelProvider(
            this,
            Injection.providePubsViewModelFactory(requireContext())
        )[PubsViewModel::class.java]

        loadData()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPubsBinding.inflate(inflater, container, false)
        return binding.root
    }

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
            bnd.logout.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                Navigation.findNavController(it).navigate(R.id.action_to_login)
            }

            bnd.swipeRefresh.setOnRefreshListener {
                loadData()
                viewModel.refreshData()
            }

            bnd.friends.setOnClickListener {
                it.findNavController().navigate(R.id.action_to_friends)
            }

            bnd.sortName.setOnClickListener {
                viewModel.sortPubs("name")
                bnd.sortName.isEnabled = false
                bnd.sortDist.isEnabled = true
                bnd.sortUsers.isEnabled = true
                bnd.sortType.isEnabled = true
            }

            bnd.sortDist.setOnClickListener {
                viewModel.sortPubs("dist")
                bnd.sortDist.isEnabled = false
                bnd.sortName.isEnabled = true
                bnd.sortUsers.isEnabled = true
                bnd.sortType.isEnabled = true
            }

            bnd.sortUsers.setOnClickListener {
                viewModel.sortPubs("users")
                bnd.sortUsers.isEnabled = false
                bnd.sortDist.isEnabled = true
                bnd.sortName.isEnabled = true
                bnd.sortType.isEnabled = true
            }

            bnd.sortType.setOnClickListener {
                viewModel.sortPubs("type")
                bnd.sortType.isEnabled = false
                bnd.sortDist.isEnabled = true
                bnd.sortName.isEnabled = true
                bnd.sortUsers.isEnabled = true
            }

            bnd.sortName.isEnabled = false

            bnd.findPub.setOnClickListener {
                if (checkPermissions()) {
                    it.findNavController().navigate(R.id.action_to_locate)
                } else {
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it
        }

        viewModel.pubs.observe(viewLifecycleOwner) {
            if (viewModel.myLocation.value != null)
                viewModel.calculateDist()

        }

        viewModel.myLocation.observe(viewLifecycleOwner) {
            if (viewModel.pubs.value != null)
                viewModel.calculateDist()
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