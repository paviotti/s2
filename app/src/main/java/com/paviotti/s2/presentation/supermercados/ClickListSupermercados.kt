package com.paviotti.s2.presentation.supermercados

import com.paviotti.s2.data.model.Supermercado

interface ClickListSupermercados {
    fun onUnChkImgClick(supermercado: Supermercado)
    fun onChkImgClick(supermercado: Supermercado)
}