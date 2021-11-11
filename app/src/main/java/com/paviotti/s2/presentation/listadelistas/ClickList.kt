package com.paviotti.s2.presentation.listadelistas

import com.paviotti.s2.data.model.ListaDeListas

//interface criada para implemetar o click do botão
interface ClickList {
    fun onItemClick(itemDaLista: String, nomeDaLista: String) //vem do adaptador:  itemView.setOnClickListener { itemClickList.onItemClick(item.nome_da_lista) }
    fun onImageclick(btn_delete: ListaDeListas)
}