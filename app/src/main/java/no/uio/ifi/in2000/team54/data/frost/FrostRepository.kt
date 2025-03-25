package no.uio.ifi.in2000.team54.data.frost

class FrostRepository() {
    private val datasource: FrostDatasource = FrostDatasource()

    // Just to test connection
    suspend fun getSomethingFromDatasource(latitude: String, longtitude: String): List<String> {
        return datasource.getSomethingFromFrost(latitude, longtitude)
    }
}