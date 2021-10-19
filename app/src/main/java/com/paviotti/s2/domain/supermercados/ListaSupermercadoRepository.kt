package com.paviotti.s2.domain.supermercados

import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado

interface ListaSupermercadoRepository {
    suspend fun getLatestListaSupermercado():Result<List<Supermercado>>
}