package com.paviotti.s2.domain.lista_completa

import com.paviotti.s2.data.model.Produto
import com.paviotti.s2.data.remote.lista_completa.ListaCompletaDataSource
import com.paviotti.s2.core.Result
/** (private val datasource:ListaCompletaDataSource) é criada uma instância para pode acessar os metodos
 * inversão de controle - injeção de dependência*/
class ListaCompletaRepositoryImplement(private val datasource:ListaCompletaDataSource):ListaCompletaRepository {
    override suspend fun getLatestListaCompleta(): Result<List<Produto>> {
       return datasource.getLatestListaCompleta()
    }

    override suspend fun getLatestListaCriada(): Result<List<Produto>> {
        TODO("Not yet implemented")
    }

    override suspend fun createItemLista(newItem: Produto) {
        return datasource.createNewItem(newItem)
    }

    override suspend fun updateItemLista(produto: Produto) {
        return datasource.updateItemLista(produto)
    }

    override suspend fun updateSun(produto: Produto) {
        return datasource.updateSun(produto)
    }
}