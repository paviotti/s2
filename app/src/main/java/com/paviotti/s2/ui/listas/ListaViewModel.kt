package com.paviotti.s2.ui.listas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Listas Fragment"
    }
    val text: LiveData<String> = _text
}