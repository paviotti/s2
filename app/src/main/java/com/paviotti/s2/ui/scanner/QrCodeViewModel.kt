package com.paviotti.s2.ui.scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QrCodeViewModel : ViewModel() {

    //barra de progresso
    private val _progressState = MutableLiveData<Boolean>()
    val progressState: LiveData<Boolean> get() = _progressState

    private val _navigation = MutableLiveData<NavDirections?>()
    val navigation: LiveData<NavDirections?> get() = _navigation

    init {
        _progressState.value = false
    }

    //pesquisa uma string no codigo de barras
    fun searchBarcode(barcode: String) {
        _progressState.value = true //começa a escanear
        viewModelScope.launch {
            delay(1000)
            //safeArgs (make project) =
            _navigation.value =
                    // primeiro fragmento + directions (.) action para o segundo fragmento passando o qrCode
                QrCodeFragmentDirections.actionNavQrCodeToSuccessScanFragment(barcode)
            _progressState.value = false //para de scanear
        }
    }

    fun doneNavigating() {
        _navigation.value = null //limpa a navegação
    }
}