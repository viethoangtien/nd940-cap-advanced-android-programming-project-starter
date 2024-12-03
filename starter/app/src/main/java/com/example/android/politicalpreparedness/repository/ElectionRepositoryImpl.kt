package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election

class ElectionRepositoryImpl(
    private val electionDatabase: ElectionDatabase, private val civicsApiService: CivicsApiService
) : ElectionRepository {

    override suspend fun getUpcomingElections(): List<Election> {
        return civicsApiService.getElections().elections
    }
}