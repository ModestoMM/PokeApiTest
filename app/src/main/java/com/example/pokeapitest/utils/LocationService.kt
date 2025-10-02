package com.example.pokeapitest.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationService(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateDistanceMeters(10f) // <-- Corrección aquí
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    trySend(it)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}