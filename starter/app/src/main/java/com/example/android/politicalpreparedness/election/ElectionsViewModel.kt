package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ElectionsViewModel(private val electionRepository: ElectionRepository) : ViewModel() {

    private val _electionsLiveData: MutableLiveData<List<Election>> = MutableLiveData()
    val electionsLiveData: LiveData<List<Election>> = _electionsLiveData

    fun getUpcomingElections() {
        Timber.d("getUpcomingElections starting")
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    electionRepository.getUpcomingElections()
                }.let { elections ->
                    _electionsLiveData.value = elections
                }
            }.getOrElse {
                Timber.d("getUpcomingElections Exception: $it")
            }
        }
    }

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}