package com.example.android.politicalpreparedness.repository

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election

class ElectionRepositoryImpl(
    private val context: Context
) : ElectionRepository {

    private val electionDatabase: ElectionDatabase by lazy {
        ElectionDatabase.getInstance(context)
    }

    private val civicsApiService: CivicsApiService by lazy {
        CivicsApi.retrofitService
    }

    override suspend fun getUpcomingElections(): List<Election> {
        return civicsApiService.getElections().elections
    }
}