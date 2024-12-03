package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import com.example.android.politicalpreparedness.repository.PoliticalRepositoryImpl

object ServiceLocator {
    private var database: ElectionDatabase? = null
    private var apiService: CivicsApiService? = null

    @Volatile
    var politicalRepository: PoliticalRepository? = null

    fun provideElectionRepository(context: Context): PoliticalRepository {
        synchronized(this) {
            return politicalRepository ?: createElectionRepository(context)
        }
    }

    private fun createElectionRepository(context: Context): PoliticalRepository {
        val newRepo = PoliticalRepositoryImpl(
            electionDatabase = createDataBase(context),
            civicsApiService = createCivicsApiService(context)
        )
        politicalRepository = newRepo
        return newRepo
    }

    private fun createCivicsApiService(context: Context): CivicsApiService {
        return apiService ?: CivicsApi.retrofitService
    }

    private fun createDataBase(context: Context): ElectionDatabase {
        return database ?: ElectionDatabase.getInstance(context)
    }
}