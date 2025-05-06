package no.uio.ifi.in2000.team54.ui.home

import androidx.annotation.DrawableRes

data class NavItem (
    @DrawableRes val selected: Int,
    @DrawableRes val unselected: Int,
    val route: String
)