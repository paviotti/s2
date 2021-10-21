package com.paviotti.s2.data.model

data class Supermercado(
    val id_supermercado: String = "",
    val nome_emitente: String = "",
    val nome_fantasia: String = "",
    val cnpj: String = "",
    val endereco: String = "",
    val cidade: String = "",
    val uf: String = "",
    var selecionado: Boolean = false
)