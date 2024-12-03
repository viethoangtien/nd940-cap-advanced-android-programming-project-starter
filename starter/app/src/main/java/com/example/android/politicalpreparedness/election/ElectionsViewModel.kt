package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ElectionsViewModel(private val politicalRepository: PoliticalRepository) : ViewModel() {

    private val _electionsLiveData: MutableLiveData<List<Election>> = MutableLiveData()
    val electionsLiveData: LiveData<List<Election>> = _electionsLiveData

    fun getUpcomingElections() {
        Timber.d("getUpcomingElections starting")
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    politicalRepository.getUpcomingElections()
                }.let { elections ->
                    _electionsLiveData.value = elections
                }
            }.getOrElse {
                Timber.e("getUpcomingElections Exception: $it")
            }
        }
    }

    fun getAllElection() = politicalRepository.getAllElection()

}