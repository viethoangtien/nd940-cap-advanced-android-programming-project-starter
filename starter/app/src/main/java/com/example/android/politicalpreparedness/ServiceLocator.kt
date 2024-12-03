package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.repository.ElectionRepositoryImpl

object ServiceLocator {
    private var database: ElectionDatabase? = null
    private var apiService: CivicsApiService? = null

    @Volatile
    var electionRepository: ElectionRepository? = null

    fun provideElectionRepository(context: Context): ElectionRepository {
        synchronized(this) {
            return electionRepository ?: createElectionRepository(context)
        }
    }

    private fun createElectionRepository(context: Context): ElectionRepository {
        val newRepo = ElectionRepositoryImpl(
            electionDatabase = createDataBase(context),
            civicsApiService = createCivicsApiService(context)
        )
        electionRepository = newRepo
        return newRepo
    }

    private fun createCivicsApiService(context: Context): CivicsApiService {
        return apiService ?: CivicsApi.retrofitService
    }

    private fun createDataBase(context: Context): ElectionDatabase {
        return database ?: ElectionDatabase.getInstance(context)
    }
}