package no.uio.ifi.in2000.team54

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import no.uio.ifi.in2000.team54.data.shared.RepositoryProvider
import no.uio.ifi.in2000.team54.data.shared.SharedRepository
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {
    @Inject
    lateinit var sharedRepository: SharedRepository

    override fun onCreate() {
        super.onCreate()
        // Hilt will inject this
        RepositoryProvider.sharedRepository = sharedRepository
    }
}