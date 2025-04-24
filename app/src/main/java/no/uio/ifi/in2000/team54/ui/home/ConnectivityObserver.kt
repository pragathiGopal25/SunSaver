package no.uio.ifi.in2000.team54.ui.home

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    val isConnected: Flow<Boolean>
}