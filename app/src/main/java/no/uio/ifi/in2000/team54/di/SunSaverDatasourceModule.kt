package no.uio.ifi.in2000.team54.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team54.data.shared.ISunSaverDatasource
import no.uio.ifi.in2000.team54.data.shared.SunSaverDatasource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SunSaverDatasourceModule {

    @Binds
    @Singleton
    abstract fun bindSunSaverDatasource(
        sunSaverDatasource: SunSaverDatasource
    ): ISunSaverDatasource
}