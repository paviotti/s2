package com.paviotti.s2.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.paviotti.s2.R
import com.paviotti.s2.core.Base.BaseViewHolder
import com.paviotti.s2.data.model.Supermercado
import com.paviotti.s2.databinding.CardListaCompletaBinding
import com.paviotti.s2.databinding.CardNomesSupermercadoBinding
import com.paviotti.s2.presentation.supermercados.ClickListSupermercados

class ListaSupermercadosAdapter(
   // private val listSupermarket: List<Supermercado>,
    private val listSupermarket: List<Supermercado>,
    private val itemClickList: ClickListSupermercados
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    /** cria o post, ou seja a telinha com todas as informações*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        /** infla a tela xml da lista*/
        val itemBinding = CardNomesSupermercadoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        val holder = ListaSupermercadosViewHolder(itemBinding, parent.context)
        /** pega o click imgChkSelected do card xml*/
        itemBinding.imgUnChkSelected.setOnClickListener {
            val position = holder.bindingAdapterPosition.takeIf { it!= DiffUtil.DiffResult.NO_POSITION }
                ?: return@setOnClickListener
            itemClickList.onImgClick(listSupermarket[position]) //passa a lista de supermercados ao clicar na imagem
            if(listSupermarket[position].selecionado){
                //fazer alteração para mudar estatus
            }
            itemBinding.imgUnChkSelected.visibility = View.INVISIBLE
            itemBinding.imgChkSelected.visibility = View.VISIBLE
            listSupermarket[position].selecionado=true
            notifyDataSetChanged() //redesenha a reciclerView = https://www.youtube.com/watch?v=0SdXoQ1g7RQ
        }
        return holder //retorna ListaSupermercadosViewHolder-> holder.bind(listSupermarket[position])
    }

    /** cria o holder (cardView)*/
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is ListaSupermercadosViewHolder-> holder.bind(listSupermarket[position])
        }
    }

    override fun getItemCount(): Int = listSupermarket.size

    private inner class ListaSupermercadosViewHolder(
        val binding: CardNomesSupermercadoBinding,
        val Context: Context
    ) : BaseViewHolder<Supermercado>(binding.root) {
        override fun bind(item: Supermercado) {
            binding.nomeFantasia.text = item.nome_fantasia
            binding.endereco.text = item.endereco
            binding.cidade.text = item.cidade
            binding.uf.text = item.uf
            Log.d("icone", "SupSelecionado:${item.nome_fantasia} = ${item.selecionado}")
        }

    }
}