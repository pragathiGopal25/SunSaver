package no.uio.ifi.in2000.team54.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.data.frost.FrostRepository


class HomeScreenViewModel: ViewModel() {
    private val _repository = FrostRepository()

    // just to test connection
    init {
        getSomethingFromRepository()
    }
    private fun getSomethingFromRepository() {
        viewModelScope.launch {
            val list = _repository.getSomethingFromDatasource("10.72", "59.9423")
            Log.i("test", list.toString())
        }
    }
}