package com.paviotti.s2.presentation.supermercados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.paviotti.s2.core.Result
import com.paviotti.s2.domain.supermercados.ListaSupermercadoRepository
import kotlinx.coroutines.Dispatchers

class ListaSupermercadosViewModel(private val repository: ListaSupermercadoRepository) :
    ViewModel() {


    fun fetchLatestList() = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(repository.getLatestListaSupermercado())
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}

class ListaSupermercadosViewModelFactory(private val repository: ListaSupermercadoRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ListaSupermercadoRepository::class.java)
            .newInstance(repository)
    }

}