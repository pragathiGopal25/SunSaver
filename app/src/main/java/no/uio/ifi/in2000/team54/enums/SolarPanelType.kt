package no.uio.ifi.in2000.team54.enums

enum class SolarPanelType(
    val displayName: String,
    val watt: Int
) {

    PREMIUM(
        "Premium",
        405
    ),
    PERFORMANCE(
        "Performance",
        435
    ),
    ECONOMY(
        "Economy",
        455
    );

    fun nameWithWatt(): String {
        return "${this.displayName} (${this.watt}W)"
    }
}