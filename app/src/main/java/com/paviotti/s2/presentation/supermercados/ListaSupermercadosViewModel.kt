package com.paviotti.s2.presentation.supermercados

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado
import com.paviotti.s2.data.model.User
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

    fun updateItem(supermercado: Supermercado) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repository.updateItem(supermercado)))
        }catch (e: Exception){
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