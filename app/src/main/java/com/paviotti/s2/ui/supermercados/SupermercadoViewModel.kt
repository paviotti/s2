package com.paviotti.s2.ui.supermercados

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SupermercadoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Supermercado Fragment"
    }
    val text: LiveData<String> = _text
}