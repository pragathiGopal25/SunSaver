package no.uio.ifi.in2000.team54

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import no.uio.ifi.in2000.team54.data.shared.RepositoryProvider
import no.uio.ifi.in2000.team54.data.shared.SunSaverRepository
import javax.inject.Inject

@HiltAndroidApp
class SunSaverApplication : Application() {
    @Inject
    lateinit var sunSaverRepository: SunSaverRepository

    override fun onCreate() {
        super.onCreate()
        // Hilt will inject this
        RepositoryProvider.sunSaverRepository = sunSaverRepository
    }
}