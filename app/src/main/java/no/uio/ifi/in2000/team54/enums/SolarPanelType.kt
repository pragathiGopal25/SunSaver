package no.uio.ifi.in2000.team54.enums

enum class SolarPanelType(
    val displayName: String,
    val watt: Int,
    val price: Double,
    val installationPrice: Double,
) {

    PREMIUM(
        "Premium",
        405,
        6000.0,
        3000.0
    ),
    PERFORMANCE(
        "Performance",
        435,
        6750.0,
        3250.0
    ),
    ECONOMY(
        "Economy",
        455,
        7500.0,
        3250.0
    );

    fun totalPrice(amount: Int): Double {
        return (price + installationPrice) * amount
    }

    fun nameWithWatt(): String {
        return "${this.displayName} (${this.watt}W)"
    }

    companion object {
        const val AREA = 2.5
    }
}