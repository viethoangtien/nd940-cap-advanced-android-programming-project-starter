package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.SingleLiveEvent
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class VoterInfoViewModel(private val politicalRepository: PoliticalRepository) : ViewModel() {

    var election: Election? = null

    private val _voterInfo = MutableLiveData<VoterInfo>()
    val voterInfo: LiveData<VoterInfo>
        get() = _voterInfo

    private val _allElectionLiveData = politicalRepository.getAllElection()
    val allElectionLiveData: LiveData<List<Election>> = _allElectionLiveData

    private val _isFollowElectionLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isFollowElectionLiveData: LiveData<Boolean> = _isFollowElectionLiveData

    val votingLocation: SingleLiveEvent<String> = SingleLiveEvent()
    val ballotInformation: SingleLiveEvent<String> = SingleLiveEvent()

    fun getVoterInformation() {
        viewModelScope.launch {
            election?.let { election ->
                val state = election.division.state.ifEmpty { DEFAULT_STATE }
                val address = "${state},${election.division.country}"
                runCatching {
                    withContext(Dispatchers.IO) {
                        politicalRepository.getVoterInfo(address = address, electionId = election.id)
                    }.let { voterInfo ->
                        _voterInfo.value = voterInfo
                    }
                }.getOrElse {
                    Timber.e("getVoterInfo Exception: $it")
                }
            }
        }

    }

    fun showVotingLocation() {
        _voterInfo.value?.votingLocationUrl?.let {
            votingLocation.value = it
        }
    }

    fun showBallotInformation() {
        _voterInfo.value?.ballotInformationUrl?.let {
            ballotInformation.value = it
        }
    }

    fun buttonClicked() {
        viewModelScope.launch {
            election?.let { election ->
                val isFollowElection = _isFollowElectionLiveData.value ?: false
                if (isFollowElection) {
                    politicalRepository.deleteElection(election)
                } else {
                    politicalRepository.insert(election)
                }
            }
        }
    }

    fun updateFollowStatus(elections: List<Election>?) {
        elections?.let {
            Timber.d("getAllElection: $elections")
            _isFollowElectionLiveData.value = elections.any { it.id == this.election?.id }
        }
    }

    companion object {
        private const val DEFAULT_STATE = "ga"
    }
}