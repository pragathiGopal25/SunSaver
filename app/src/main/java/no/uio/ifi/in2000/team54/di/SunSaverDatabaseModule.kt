package no.uio.ifi.in2000.team54.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team54.database.SunSaverDao
import no.uio.ifi.in2000.team54.database.SunSaverDatabase
import javax.inject.Singleton


@Module // depends on Room implementatations, therefore need to create a Module for this one
@InstallIn(SingletonComponent::class)
object SunSaverDatabaseModule {

    @Provides
    @Singleton
    fun provideSunSaverDatabase(
        @ApplicationContext context: Context // ApplicationContext so Hilt knows to inject the correct Context
    ): SunSaverDatabase {
        return Room.databaseBuilder(
            context,
            SunSaverDatabase::class.java,
            "SunSaverDatabase")
            .build()
    }

    @Provides
    fun provideSunSaverDao(
        database: SunSaverDatabase
    ): SunSaverDao {
        return database.sunSaverDao()
    }
}