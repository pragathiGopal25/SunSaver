package no.uio.ifi.in2000.team54.ui.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    val isConnected: Flow<Boolean>
}