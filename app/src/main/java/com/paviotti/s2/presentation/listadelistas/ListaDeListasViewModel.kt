package com.paviotti.s2.presentation.listadelistas

import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.ListaDeListas
import com.paviotti.s2.domain.listadelistas.ListaDeListasRepository
import com.paviotti.s2.ui.listas.listas_de_listas.ListaDeListasFragmentDirections
import kotlinx.coroutines.Dispatchers

//https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/24810842#overview
/** ListaDeListasViewModel recebe <-  ListaDeListasRepositoryFactory */
class ListaDeListasViewModel(private val repository: ListaDeListasRepository) : ViewModel() {


    //safeArgs, para passar o nome da lista para listaCompleta
    private val _navigationTolist = MutableLiveData<NavDirections?>()
    val navigationTolist: LiveData<NavDirections?> get() = _navigationTolist


    //fetch = buscar | busca em segundo plano para não bloquear o app
    fun fetchLatestList() = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(repository.getLatestListas())
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
    //apaga uma lista
    fun deleteList(btnDelete: ListaDeListas) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repository.deleteList(btnDelete)))
        }catch (e:java.lang.Exception){
            emit(Result.Failure(e))
        }

    }

    //https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/26511092#announcements
    //recebe os dados do Fragment
    fun createNewItem(newItem: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            //  Log.d("ViewModel","viewModel: $newItem")
            emit(Result.Success(repository.createNewItem(newItem)))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}

class ListaDeListasViewModelFactory(private val repository: ListaDeListasRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ListaDeListasRepository::class.java)
            .newInstance(repository)
    }

}