package com.paviotti.s2.domain.lista_completa

import com.paviotti.s2.data.model.Produto
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.SomaLista

interface ListaCompletaRepository {
    /** possui um método que vai buscar informações no servidor
     * o metodo pega as ultimas listas, mais atualizadas em (MODEL)
     * RETORNA lIST RESULT */
    suspend fun getLatestListaCompleta():Result<List<Produto>> //lê produtos/ em Firebase
    suspend fun getLatestListaCriada():Result<List<Produto>> //lê users/listas_de_compras/
    suspend fun createItemLista(produto: Produto) //grava users/listas_de_compras/"nome da lista selecionada"
    suspend fun updateItemLista(produto: Produto) //atualiza a lista selecionada
    fun updateSun(produto: Produto) //atulaiza soma
}

/**
 * Deve ler a tabela de produtos que possui todos os itens e cada item selecionado pelo usuário
 * vai ser gravado na tabela propria dele em o nome da lista de compras selecionado vindo de ListasDeListas
 * */