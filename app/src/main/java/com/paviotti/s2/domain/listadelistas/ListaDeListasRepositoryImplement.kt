package com.paviotti.s2.domain.listadelistas

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.ListaDeListas
import com.paviotti.s2.data.remote.ListaDeListas.ListaDeListasDataSource

class ListaDeListasRepositoryImplement(private val dataSource: ListaDeListasDataSource) :
    ListaDeListasRepository {
    override suspend fun getLatestListas(): Result<List<ListaDeListas>> =
        dataSource.getLatestListas()

    //busca um metodo em ListaDeListasDataSource
    override suspend fun createNewItem(newItem: String) {
       // Log.d("Var","implement: $newItem")
        return dataSource.createNewItem(newItem)
    }

    override suspend fun deleteList(btnDelete: ListaDeListas) {
        return dataSource.deleteList(btnDelete)
    }

}