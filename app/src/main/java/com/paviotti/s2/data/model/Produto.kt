package com.paviotti.s2.data.model

data class Produto(
    var id: String = "",
    val categoria: String = "",
    val codigo_barras: String = "",
    val descricao: String = "",
    var quantidade: Double = 0.0,
    val unidade: String = "",
    var valor_s1: Double = 0.0,
    var valor_s2: Double = 0.0,
    var valor_s3: Double = 0.0,
    val usuario: String = "",
    var photo_url: String = "",
    var include_item: Boolean = false
)