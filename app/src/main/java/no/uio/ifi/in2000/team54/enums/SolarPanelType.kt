package no.uio.ifi.in2000.team54.enums


enum class SolarPanelType(
    val displayName: String,
    val watt: Int,
    val price: Double,
    val installationPrice: Double,
    val length: Double, // values found on the internet for similar wattage panels
    val width: Double
) {

    ECONOMY(
        "Economy",
        405,
        6000.0,
        3000.0,
        length = 1.72,
        width = 1.13
    ),
    PERFORMANCE(
        "Performance",
        435,
        6750.0,
        3250.0,
        length =1.74,
        width =  1.09
    ),
    PREMIUM(
        "Premium",
        455,
        7500.0,
        3250.0,
        length = 2.10,
        width = 1.04
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