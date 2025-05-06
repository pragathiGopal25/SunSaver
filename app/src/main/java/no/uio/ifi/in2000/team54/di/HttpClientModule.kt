package no.uio.ifi.in2000.team54.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

@Module
@InstallIn(SingletonComponent::class)
object HttpClientModule {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    @Provides
    fun provideHttpClient(): HttpClient {
        return this.httpClient
    }
}