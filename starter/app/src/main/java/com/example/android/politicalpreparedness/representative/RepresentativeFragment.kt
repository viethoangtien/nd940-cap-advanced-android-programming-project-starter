package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.Locale

class RepresentativeFragment : Fragment() {

    private var _binding: FragmentRepresentativeBinding? = null
    private val binding get() = _binding!!

    private lateinit var representativeListAdapter: RepresentativeListAdapter

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) &&
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Precise location access granted and approximate location access granted.
            }

            else -> {
                // No location access granted.
                showSnackBarForDenied()
            }
        }
    }

    private val representativeViewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(
            this, RepresentativeViewModelFactory(application = requireActivity().application)
        )[RepresentativeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepresentativeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = representativeViewModel
        initViews()
        initAdapter()
        observeLiveData()
        representativeViewModel.initStates(
            requireActivity().resources.getStringArray(R.array.states).toList()
        )
    }

    private fun initAdapter() {
        representativeListAdapter = RepresentativeListAdapter()
        binding.recyclerRepresentatives.adapter = representativeListAdapter
    }

    private fun observeLiveData() {
        representativeViewModel.representativesLiveData.observe(viewLifecycleOwner) { representatives ->
            representativeListAdapter.submitList(representatives)
        }
    }

    private fun initViews() {
        binding.textUseLocation.setOnClickListener {
            checkLocationPermissions()
        }
        binding.textFindMyRepresentative.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun checkLocationPermissions() {
        return if (isPermissionGranted()) {
            checkDeviceLocationSettings()
        } else {
            requestForegroundPermissions()
        }
    }

    private fun isPermissionGranted(): Boolean {
        val foregroundCoarseLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ))
        val foregroundFineLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))
        Timber.d(
            "isPermissionGranted " +
                    "foregroundCoarseLocationApproved: $foregroundCoarseLocationApproved, " +
                    "foregroundFineLocationApproved: $foregroundFineLocationApproved"
        )
        return foregroundCoarseLocationApproved && foregroundFineLocationApproved
    }

    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        Timber.d("checkDeviceLocationSettings")
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 10000)
            .setWaitForAccurateLocation(false)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            Timber.e("Error getting location settings resolution: ${exception.message}")
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.e("Error getting location settings resolution: ${sendEx.message}")
                }
            } else {
                showSnackBarForEnableDeviceLocation()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            Timber.d("addOnCompleteListener isSuccessful: ${it.isSuccessful}")
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    Timber.d("getLocation lat: ${it.latitude} lng: ${it.longitude}")
                    val address = geoCodeLocation(it)
                    representativeViewModel.onUseMyLocation(address)
                }
            }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 10)
            ?.first()
            ?.let { address ->
                Address(
                    address.thoroughfare ?: "",
                    address.subThoroughfare ?: "",
                    address.locality ?: "",
                    address.adminArea ?: "",
                    address.postalCode ?: ""
                )
            }
    }

    private fun showSnackBarForDenied() {
        Timber.d("showSnackBarForExplanation")
        Snackbar.make(
            requireView(),
            R.string.permission_denied_explanation,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.settings) {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", requireActivity().application.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
    }

    private fun showSnackBarForEnableDeviceLocation() {
        Timber.d("showSnackBarForEnableDeviceLocation")
        Snackbar.make(
            requireView(),
            R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
        ).setAction(android.R.string.ok) {
            checkDeviceLocationSettings()
        }.show()
    }

    /*
 	*  Requests ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
 	*/
    @TargetApi(29)
    private fun requestForegroundPermissions() {
        Timber.d("Request foreground location permission")
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            runBlocking {
                delay(1000)
                checkDeviceLocationSettings(false)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 9001
    }
}