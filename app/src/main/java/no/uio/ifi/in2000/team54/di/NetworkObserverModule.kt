package no.uio.ifi.in2000.team54.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team54.ui.network.NetworkObserver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkObserverModule {

    @Provides
    @Singleton
    fun provideNetworkObserver(@ApplicationContext context: Context): NetworkObserver {
        return NetworkObserver(context)
    }
}