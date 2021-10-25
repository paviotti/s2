package com.paviotti.s2.presentation.lista_completa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Produto
import com.paviotti.s2.domain.lista_completa.ListaCompletaRepository
import kotlinx.coroutines.Dispatchers

/** ListaDeListasViewModel recebe <-  ListaDeListasRepositoryFactory */
class ListaCompletaViewModel(private val repository: ListaCompletaRepository) : ViewModel() {

    //fetch = buscar | busca em segundo plano para não bloquear o app
    fun fetchLatestListComplete() = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(repository.getLatestListaCompleta())
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun createNewItem(produto: Produto) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try{
            emit(Result.Success(repository.createItemLista(produto)))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }

    //atualiza a quantidade de produtos quando clicado no + ou -
    fun updateItemLista(produto: Produto) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repository.updateItemLista(produto)))
        }catch (e:Exception){
            emit(Result.Failure(e))
        }
    }



}

class ListaCompletaViewModelFactory(private val repository: ListaCompletaRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modeClass: Class<T>): T {
        return modeClass.getConstructor(ListaCompletaRepository::class.java).newInstance(repository)
    }

    fun creatNewItemList(newItem: Produto) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try{
            emit(Result.Success(repository.createItemLista(newItem)))
        }catch (e:Exception){
            emit(Result.Failure(e))
        }
    }
}