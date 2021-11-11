package com.paviotti.s2.ui.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.paviotti.s2.R
import com.paviotti.s2.core.Base.BaseViewHolder
import com.paviotti.s2.data.model.Produto
import com.paviotti.s2.databinding.CardListaCompletaBinding
import com.paviotti.s2.presentation.lista_completa.ClickListaCompleta
import kotlinx.android.synthetic.main.card_lista_completa.view.*
import com.paviotti.s2.data.model.ItemListSum as ItemListSum

/** O adaptador recebe uma lista de Produto (model)
 *  a classe implementa o RecyclerView e recebe um viewHolder
 *  o basic view holder será criado dentro CORE
 *  o asterisco permite qualquer viewHolder*/
class ListaCompletaAdapter(
    private val listOfProdutcts: List<Produto>, private val itemClickList: ClickListaCompleta
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        /** infla a tela xml da lista*/
        val itemBinding =
            CardListaCompletaBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        /** pega o click no icone adicionar/remover do botão*/
        val holder = ListaCompletaViewHolder(itemBinding, parent.context)

        /** pega o click imgInclui quantidade de itens*/
        var qte = 0.0
        //incrementa
        itemBinding.imgInclui.setOnClickListener {
            val position =
                holder.bindingAdapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener //se não for, retorne
            itemClickList.onImgClickAdd(listOfProdutcts[position]) //passa a lista de produtos ao clicar na imagem
            qte = listOfProdutcts[position].quantidade //recebe o valor do banco de dados
          //  Log.d("qte", "qte+: $qte")
            if (listOfProdutcts[position].include_item == true) {
                listOfProdutcts[position].quantidade = (++qte).toDouble()
            }
            notifyDataSetChanged() //redesenha a reciclerView

        }
        // decrementa e exclui os itens
        itemBinding.imgDelete.setOnClickListener {
            val position =
                holder.bindingAdapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener //se não for, retorne
            itemClickList.onImgClickSub(listOfProdutcts[position]) //passa a lista de produtos ao clicar na imagem
            qte = listOfProdutcts[position].quantidade //recebe o valor do banco de dados
            if (listOfProdutcts[position].include_item == true) {
                if (qte > 0) {
                    listOfProdutcts[position].quantidade = (--qte).toDouble()
                    //   Log.d("produto", "origem qte: $qte")
                }
            }
            notifyDataSetChanged() //redesenha a reciclerView

        }
        return holder
    }

    /** cria o holder (cardView)*/
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is ListaCompletaViewHolder -> holder.bind(listOfProdutcts[position])
        }
        //controla rolagem de tela
        if(position == itemCount-1){
            itemClickList.onClickScroll()
        }
    }


    override fun getItemCount(): Int = listOfProdutcts.size

    /** pega os dados de produto e insere em nova lista*/
    private inner class ListaCompletaViewHolder(
        val binding: CardListaCompletaBinding, val context: Context
    ) : BaseViewHolder<Produto>(binding.root) {
        override fun bind(item: Produto) {
            binding.txtDescricao.text = item.descricao
            binding.txtUnidade.text = item.unidade
            binding.txtPreco1.text = item.valor_s1.toString()
            binding.txtPreco2.text = item.valor_s2.toString()
            binding.txtPreco3.text = item.valor_s3.toString()
            binding.quantidade.text = item.quantidade.toString()
            swapColor(item)
          //  sum(item)
            //  itemView.img_inclui_delete.setOnClickListener {itemClickList.onImgClick(produto = Produto())}
        }

        //teria o objetivo de somar os valores, mas falhou
        val listSun: MutableList<ItemListSum> = mutableListOf()
        var itemList = ItemListSum()
        fun sum(item: Produto) {
            var tot1 = 0.0
            var tot2 = 0.0
            var tot3 = 0.0
            when {
                item.quantidade > 0.0 -> {
                    tot1 += item.valor_s1 * item.quantidade
                    tot2 += item.valor_s2 * item.quantidade
                    tot3 += item.valor_s3 * item.quantidade
                    itemList.pos = layoutPosition
                    itemList.valor1 = tot1
                    if (itemList.pos == layoutPosition) {
                        itemList.salvo = true
                        listSun.add(itemList)

//                        Log.d(
//                            "qtexx",
//                            "pos: ${itemList.pos} bool: ${itemList.salvo} val: ${itemList.valor1} item.quantidade: ${item.quantidade} tot1: $tot1 tot2: $tot2 tot3: $tot3 prd: ${item.descricao}"
//                        )
                    }

                }
            }
        }

        fun swapColor(item: Produto) {

            if (item.quantidade > 0) {
                binding.imgInclui.setImageResource(R.drawable.ic_add_circle_24_azul)
            } else {
                binding.imgInclui.setImageResource(R.drawable.ic_add_circle_24_verde)
            }
            if (item.valor_s1 == 0.0) {
                binding.txtPreco1.setTextColor(Color.RED)
            } else {
                binding.txtPreco1.setTextColor(Color.BLACK)
            }

            if (item.valor_s2 == 0.0) {
                binding.txtPreco2.setTextColor(Color.RED)
            } else {
                binding.txtPreco2.setTextColor(Color.BLACK)
            }

            if (item.valor_s3 == 0.0) {
                binding.txtPreco3.setTextColor(Color.RED)
            } else {
                binding.txtPreco3.setTextColor(Color.BLACK)
            }
        }

    }

}


