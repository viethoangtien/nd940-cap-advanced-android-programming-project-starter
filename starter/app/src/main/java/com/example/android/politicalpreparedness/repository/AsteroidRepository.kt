package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.models.Election

interface ElectionRepository {
    suspend fun getUpcomingElections(): List<Election>
}