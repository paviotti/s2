package com.paviotti.s2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paviotti.s2.core.Base.BaseViewHolder
import com.paviotti.s2.data.model.ListaDeListas
import com.paviotti.s2.databinding.ItemListaComprasBinding

/** O adaptador recebe uma lista das listas (model)
 *  a classe implementa o RecyclerView e recebe um viewHolder
 *  o basic view holder será criado dentro CORE
 *  o asterisco permite qualquer viewHolder*/
class ListaDeListasAdapter(private val listListas: List<ListaDeListas>) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

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

    /** cria o holder*/
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
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
            binding.itemDaLista.text = item.nome_da_lista //tópicos da tela
        }
    }
}