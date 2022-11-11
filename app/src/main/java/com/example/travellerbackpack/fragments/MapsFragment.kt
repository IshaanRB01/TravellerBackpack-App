package com.example.travellerbackpack.fragments

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.travellerbackpack.R
import com.example.travellerbackpack.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mancj.materialsearchbar.MaterialSearchBar
import java.io.IOException


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location

    private lateinit var _binding: FragmentMapsBinding

    private val binding get() = _binding

    private lateinit var mapView: View

    var currentMarker: Marker? = null
    lateinit var address: Address

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment != null) {
            mapView = mapFragment.requireView()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.mapSearch.setOnSearchActionListener(object :
            MaterialSearchBar.OnSearchActionListener {

            override fun onSearchStateChanged(enabled: Boolean) {

            }

            override fun onSearchConfirmed(text: CharSequence?) {

                val location: String = text.toString()
                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null

                // checking if the entered location is null or not.
                if (location != null || location == "") {
                    // on below line we are creating and initializing a geo coder.
                    val geocoder = Geocoder(requireContext())
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    // on below line we are getting the location
                    // from our list a first position.

                    if (addressList!!.isNotEmpty()) {
                        address = addressList[0]
                    } else {
                        Toast.makeText(requireContext(), "Location not Found", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    val latLng = LatLng(address.latitude, address.longitude)

                    if (currentMarker != null) {
                        currentMarker!!.remove()
                        currentMarker = null
                    }

                    if (currentMarker == null) {
                        currentMarker = map.addMarker(
                            MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .title(location.replaceFirstChar { it -> it.uppercase() })
                        )

                        closeKeybord()

                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                    }

                }
            }


            override fun onButtonClicked(buttonCode: Int) {
            }
        })

        mapFragment?.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.isBuildingsEnabled = true
        map.isTrafficEnabled = true

        val locationButton: View = mapView.findViewWithTag("GoogleMapMyLocationButton")
        alignLocationButton(locationButton)

        val compassButton: View = mapView.findViewWithTag("GoogleMapCompass")
        alignCompassButton(compassButton)

        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            lastLocation = location
            val currentLatLng = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions().position(currentLatLng).title("Your Location")
            map.addMarker(markerOptions)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
        }
    }

    private fun alignCompassButton(compassButton: View) {
        val rlp = compassButton.layoutParams as (RelativeLayout.LayoutParams)
        rlp.removeRule(RelativeLayout.ALIGN_PARENT_TOP)

        val locationButton: View = mapView.findViewWithTag("GoogleMapMyLocationButton")

        rlp.addRule(RelativeLayout.ABOVE, locationButton.id)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_START)
        rlp.setMargins(0, 0, 0, 30)

    }

    private fun alignLocationButton(locationButton: View) {
        val rlp = locationButton.layoutParams as (RelativeLayout.LayoutParams)
        rlp.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
        rlp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        rlp.removeRule(RelativeLayout.ALIGN_PARENT_END)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        rlp.setMargins(30, 0, 0, 30)
    }

    private fun closeKeybord() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = requireActivity().currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(requireActivity())
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Toast.makeText(requireContext(), marker.title, Toast.LENGTH_SHORT).show()
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 12f))
        return true
    }

}