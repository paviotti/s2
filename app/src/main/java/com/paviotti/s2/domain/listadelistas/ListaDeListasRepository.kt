package com.paviotti.s2.domain.listadelistas

import com.google.firebase.auth.FirebaseUser
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.ListaDeListas

/** possui um método que vai buscar informações no servidor
 * o metodo pega as ultimas listas, mais atualizadas em (MODEL)
 * RETORNA lIST RESULT */
interface ListaDeListasRepository {
    suspend fun getLatestListas(): Result<List<ListaDeListas>>
    //recebe os dados de viewModel
    suspend fun createNewItem(newItem: String) //:FirebaseUser? //pode ou não retornar nada
    suspend fun deleteList(btnDelete: ListaDeListas) //apaga uma lista completa
}