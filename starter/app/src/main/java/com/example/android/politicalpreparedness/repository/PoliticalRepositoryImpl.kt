package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.Office
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.network.models.toVoterInfo
import com.example.android.politicalpreparedness.representative.model.Representative

class PoliticalRepositoryImpl(
    private val electionDatabase: ElectionDatabase, private val civicsApiService: CivicsApiService
) : PoliticalRepository {

    override suspend fun getUpcomingElections(): List<Election> {
        return civicsApiService.getElections().elections
    }

    override suspend fun getVoterInfo(address: String, electionId: Int): VoterInfo {
        return civicsApiService.getVoterInfo(address, electionId).toVoterInfo()
    }

    override fun getAllElection(): LiveData<List<Election>> =
        electionDatabase.electionDao.getAllElection()

    override suspend fun deleteElection(election: Election) =
        electionDatabase.electionDao.delete(election)

    override suspend fun insert(election: Election) = electionDatabase.electionDao.insert(election)

    override suspend fun getRepresentatives(address: String): List<Representative> {
        return civicsApiService.getRepresentatives(address).let { representativeResponse ->
            representativeResponse.offices.flatMap { office ->
                office.getRepresentatives(representativeResponse.officials)
            }
        }
    }
}