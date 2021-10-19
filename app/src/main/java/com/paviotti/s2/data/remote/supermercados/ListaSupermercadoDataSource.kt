package com.paviotti.s2.data.remote.supermercados

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado
import kotlinx.coroutines.tasks.await

class ListaSupermercadoDataSource {
    suspend fun getLatestListaSupermercado(): Result<List<Supermercado>> {
        val listSupermarket = mutableListOf<Supermercado>()
        val user = FirebaseAuth.getInstance().currentUser
        val referenceSupermarket = FirebaseFirestore.getInstance().collection("supermercados")
        val querySnapshot = referenceSupermarket.get().await()
        for (itemList in querySnapshot.documents) {
            itemList.toObject(Supermercado::class.java)?.let { itens ->
                listSupermarket.add(itens)
            }
        }
        return Result.Success(listSupermarket)
    }
}