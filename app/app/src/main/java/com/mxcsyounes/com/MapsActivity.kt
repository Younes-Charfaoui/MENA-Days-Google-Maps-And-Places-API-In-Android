package com.mxcsyounes.com

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var placesAutoCompleteFragment: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        initializePlacesAPI()

        initializingPlacesAutoCompleteFragment()

        bindMapFragment()
    }

    private fun initializePlacesAPI() {
        if (!Places.isInitialized())
            Places.initialize(this, getString(R.string.google_maps_key))
    }

    private fun initializingPlacesAutoCompleteFragment() {
        placesAutoCompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        placesAutoCompleteFragment.setPlaceFields(
            arrayListOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        placesAutoCompleteFragment.setOnPlaceSelectedListener(createPlacesSelectionListener())
    }

    private fun createPlacesSelectionListener(): PlaceSelectionListener {
        return object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                handlePlacesSelection(place)
            }

            override fun onError(status: Status) {
                handleErrorOnPlacesSelection(status)
            }
        }
    }

    private fun handlePlacesSelection(place: Place) {
        val latLng = place.latLng
        try {
            animateCamera(latLng!!)
            addMarkerToMapAndClear(latLng, place)
        } catch (exception: KotlinNullPointerException) {
            Log.d(MapsActivity::class.java.name, "Error happen: latlng was null")
        }
    }

    private fun handleErrorOnPlacesSelection(status: Status) {
        Log.d("MapsActivity", "${status.statusMessage}")
    }

    private fun addMarkerToMapAndClear(latLng: LatLng, place: Place) {
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(latLng).title(place.name))
    }

    private fun animateCamera(latLng: LatLng) {
        googleMap.animateCamera(createCameraPosition(latLng))
    }

    private fun createCameraPosition(latLng: LatLng): CameraUpdate {
        return CameraUpdateFactory.newCameraPosition(
            CameraPosition.Builder().target(latLng).zoom(
                10.0f
            ).build()
        )
    }

    private fun bindMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val sydney = LatLng(28.0339, 1.6596)
        this.googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Algeria"))
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
