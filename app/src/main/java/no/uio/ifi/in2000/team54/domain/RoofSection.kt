package no.uio.ifi.in2000.team54.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoofSection(
    val id: Long?,
    val area: Double,
    val incline: Double,
    val direction: Double,
    val panels: Int,
    val mapId: String?,
) : Parcelable