package com.paviotti.s2.domain.supermercados

import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado
import com.paviotti.s2.data.model.User

interface ListaSupermercadoRepository {
    suspend fun getLatestListaSupermercado():Result<List<Supermercado>>
    suspend fun updateItem(supermercado: Supermercado)
    suspend fun insertUser(supermercado: Supermercado)
}