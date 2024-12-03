package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class RepresentativeViewModel(private val politicalRepository: PoliticalRepository) : ViewModel() {

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _states = MutableLiveData<List<String>>()
    val states: LiveData<List<String>>
        get() = _states

    private val _representativesLiveData = MutableLiveData<List<Representative>>()
    val representativesLiveData: LiveData<List<Representative>>
        get() = _representativesLiveData

    private var stateList: List<String> = listOf()

    var indexSelected = MutableLiveData(0)

    fun onUseMyLocation(address: Address?) {
        address?.let {
            val indexOfState = _states.value?.indexOf(address.state)
            if (indexOfState != null && indexOfState >= 0) {
                indexSelected.value = indexOfState
                _address.value = it
                getRepresentatives()
            }
        }
    }

    private fun getRepresentatives() {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val address = _address.value
                    return@withContext if (address != null) {
                        val stateSelected = stateList[indexSelected.value ?: 0]
                        address.state = stateSelected
                        politicalRepository.getRepresentatives(address = address.toFormattedString())
                    } else emptyList()
                }.let { representatives ->
                    _representativesLiveData.value = representatives
                }
            }.getOrElse {
                Timber.e("getRepresentatives Exception: $it")
            }
        }
    }

    fun initStates(states: List<String>) {
        stateList = states
        _states.value = states
    }
}
