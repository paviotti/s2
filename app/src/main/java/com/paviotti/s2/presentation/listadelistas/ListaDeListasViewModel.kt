package com.paviotti.s2.presentation.listadelistas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.paviotti.s2.core.Result
import com.paviotti.s2.domain.listadelistas.ListaDeListasRepository
import kotlinx.coroutines.Dispatchers
//https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/24810842#overview
/** ListaDeListasViewModel recebe <-  ListaDeListasRepositoryFactory */
class ListaDeListasViewModel(private val repository: ListaDeListasRepository) : ViewModel() {

    //fetch = buscar | busca em segundo plano para não bloquear o app
    fun fetchLatestList() = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(repository.getLatestListas())
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    //https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/26511092#announcements
   //recebe os dados do Fragment
    fun createNewItem(newItem: String) = liveData(Dispatchers.IO){
        emit(Result.Loading())
        try {
          //  Log.d("ViewModel","viewModel: $newItem")
            emit(Result.Success(repository.createNewItem(newItem)))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }
}
class ListaDeListasViewModelFactory(private val repository: ListaDeListasRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ListaDeListasRepository::class.java).newInstance(repository)
    }

}