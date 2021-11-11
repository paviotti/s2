package com.paviotti.s2.presentation.lista_completa

import com.paviotti.s2.data.model.Produto

interface ClickListaCompleta {
    fun onImgClickAdd(produto: Produto)
    fun onImgClickSub(produto: Produto)
    fun onClickScroll()
}