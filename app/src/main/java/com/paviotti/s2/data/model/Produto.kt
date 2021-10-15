package com.paviotti.s2.data.model

data class Produto(
    var id: String = "",
    val categoria: String = "",
    val codigo_barras: String = "",
    val descricao: String = "",
    val quantidade: Double = 0.0,
    val unidade: String = "",
    val valor_unitario: Double = 0.0,
    val valor_tributo: Double = 0.0,
    val valor_do_item: Double = 0.0,
    val photo_url: String = "",
    var include_item: Boolean = false
)