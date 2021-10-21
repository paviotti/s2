package com.paviotti.s2.domain.supermercados

import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado
import com.paviotti.s2.data.remote.supermercados.ListaSupermercadoDataSource

class ListaSupermercadoRepositoryImplement(private val dataSource: ListaSupermercadoDataSource) :
    ListaSupermercadoRepository {
    override suspend fun getLatestListaSupermercado(): Result<List<Supermercado>> {
        return dataSource.getLatestListaSupermercado()
    }

    override suspend fun updateItem(supermercado: Supermercado) {
       return dataSource.updateItem(supermercado)
    }
}