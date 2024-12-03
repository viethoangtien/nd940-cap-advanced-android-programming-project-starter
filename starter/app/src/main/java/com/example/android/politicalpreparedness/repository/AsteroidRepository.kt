package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo

interface ElectionRepository {
    suspend fun getUpcomingElections(): List<Election>
    suspend fun getVoterInfo(address: String, electionId: Int): VoterInfo
    fun getAllElection(): LiveData<List<Election>>
    suspend fun deleteElection(election: Election)
    suspend fun insert(election: Election)
}