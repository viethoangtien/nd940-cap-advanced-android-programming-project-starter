package com.example.android.politicalpreparedness.network.models

data class VoterInfo(
    val election: Election?,
    val votingLocationUrl: String?,
    val ballotInformationUrl: String?
)