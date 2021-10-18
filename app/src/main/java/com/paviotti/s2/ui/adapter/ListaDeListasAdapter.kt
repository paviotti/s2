package com.paviotti.s2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.paviotti.s2.R
import com.paviotti.s2.core.Base.BaseViewHolder
import com.paviotti.s2.data.model.ListaDeListas
import com.paviotti.s2.databinding.ItemListaComprasBinding
import com.paviotti.s2.presentation.listadelistas.ClickList
import com.paviotti.s2.presentation.listadelistas.ListaDeListasViewModel
import kotlinx.android.synthetic.main.item_lista_compras.view.*

/** O adaptador recebe uma lista das listas (model)
 *  a classe implementa o RecyclerView e recebe um viewHolder
 *  o basic view holder será criado dentro CORE
 *  o asterisco permite qualquer viewHolder
 *  O Click foi implementado para enviar para outra tela*/
class ListaDeListasAdapter(
    private val listListas: List<ListaDeListas>,
    private val itemClickList: ClickList
) : RecyclerView.Adapter<BaseViewHolder<*>>() {


    /** cria o post, ou seja a telinha com todas as informações*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        /** infla a tela xml da lista*/
        val itemBinding =
            ItemListaComprasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListaDeListasViewHolder(
            itemBinding,
            parent.context
        ) //chama a classe e passa 2 parametros
    }

    /** cria o holder (cardView)*/
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is ListaDeListasViewHolder -> holder.bind(listListas[position])
        }
    }

    override fun getItemCount(): Int = listListas.size

    /** acrescentar esta classe com as iniciais da classe ListaDeListasFragment.kt
     * recebe dois parametros
     * é ela quem passa os itens do MODEL para BaseViewHolder*/
    private inner class ListaDeListasViewHolder(
        val binding: ItemListaComprasBinding,
        val context: Context
    ) : BaseViewHolder<ListaDeListas>(binding.root) {
        /** implementa o metodo bind para passar os itens da lista, ela recebe em(item: ListaDeListas)*/
        override fun bind(item: ListaDeListas) {
            //https://www.youtube.com/watch?v=eaMj60Lb05Q
            //click no cardView
            binding.itemDaLista.text = item.nome_da_lista //tópicos da tela - não mexa aqui
            if (item.btn_delete) {
                //precisa descomentar no item_lista_compras
             //   binding.imageDelete.setImageResource(R.drawable.ic_delete_24)
            }
            itemView.item_da_lista.setOnClickListener { itemClickList.onItemClick(item.nome_da_lista) }
            //precisa descomentar no item_lista_compras
           // itemView.image_delete.setOnClickListener { itemClickList.onImageclick(item.btn_delete) }
        }
    }

}