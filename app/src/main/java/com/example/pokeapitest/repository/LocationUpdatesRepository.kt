package com.example.pokeapitest.repository

import android.location.Location
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationUpdatesRepository @Inject constructor() {
    private val _locationFlow = MutableSharedFlow<Location>()
    val locationFlow: SharedFlow<Location> = _locationFlow.asSharedFlow()

    suspend fun emitNewLocation(location: Location) {
        _locationFlow.emit(location)
    }
}