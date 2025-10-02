package com.example.pokeapitest.presentation

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokeapitest.data.database.Pokemon.PokemonEntity
import com.example.pokeapitest.repository.PokemonRepository
import com.example.pokeapitest.utils.ConnectionStatus
import com.example.pokeapitest.utils.LocationService
import com.example.pokeapitest.utils.NetworkConnectivityObserver
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val connectivityObserver = NetworkConnectivityObserver(application)
    private val _networkStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Connected)
    val networkStatus: StateFlow<ConnectionStatus> = _networkStatus


    val pokemonPagingFlow: Flow<PagingData<PokemonEntity>> =
        repository.getPokemonList().cachedIn(viewModelScope)

    val favoritePokemons: Flow<List<PokemonEntity>> = repository.getFavoritePokemons()

    private val pokemonId: Int = savedStateHandle.get<Int>("pokemonId") ?: 0
    val pokemon: Flow<PokemonEntity> = repository.getPokemonById(pokemonId)

    private val _randomPokemon = MutableStateFlow<PokemonEntity?>(null)
    val randomPokemon: StateFlow<PokemonEntity?> = _randomPokemon

    private val _showAlert = MutableStateFlow(false)
    val showAlert: StateFlow<Boolean> = _showAlert

    private val _vibrateChannel = Channel<Unit>()
    val vibrateFlow: Flow<Unit> = _vibrateChannel.receiveAsFlow()

    private val locationService =
        LocationService(application, LocationServices.getFusedLocationProviderClient(application))

    private var lastLocation: Location? = null
    private var accumulatedDistance: Float = 0f

    init {
        getNewRandomPokemon()
        viewModelScope.launch {
            connectivityObserver.observe().collectLatest { status ->
                _networkStatus.value = status
            }
            locationService.getLocationUpdates().collect { newLocation ->
                if (lastLocation != null) {
                    val distance = lastLocation!!.distanceTo(newLocation)
                    if (distance >= 10) {
                        _showAlert.value = true
                    }
                }
                lastLocation = newLocation
            }
        }
    }

    fun toggleFavorite(pokemonId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(pokemonId, isFavorite)
        }
    }

    fun getNewRandomPokemon() {
        viewModelScope.launch {
            val pokemon = repository.getRandomPokemon()
            _randomPokemon.value = pokemon
            // Disparar el evento de vibración
            _vibrateChannel.send(Unit)
        }
    }

    fun hideAlert() {
        _showAlert.value = false
    }

    fun startLocationUpdates() {
        viewModelScope.launch {
            locationService.getLocationUpdates().collect { newLocation ->
                if (lastLocation == null) {
                    lastLocation = newLocation
                } else {
                    val distance = lastLocation!!.distanceTo(newLocation)
                    // Sumar la distancia si la precisión es buena
                    if (newLocation.accuracy <= 10) {
                        accumulatedDistance += distance
                        lastLocation = newLocation
                    }
                    Log.d("RandomPokemonVM", "Distancia acumulada: $accumulatedDistance metros.")

                    if (accumulatedDistance >= 10) {
                        _showAlert.value = true
                        accumulatedDistance = 0f // <-- Reiniciar el acumulador
                    }
                }
            }
        }
    }
}