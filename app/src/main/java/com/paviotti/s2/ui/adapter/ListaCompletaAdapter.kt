package com.paviotti.s2.ui.adapter

import android.content.Context
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

        /** pega o click imgInclui*/
        var qte = 0.0
        //============== incrementa =================================
        itemBinding.imgInclui.setOnClickListener {
            val position =
                holder.bindingAdapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener //se não for, retorne

            qte = listOfProdutcts[position].quantidade //recebe o valor do banco de dados
            if (listOfProdutcts[position].include_item == true) {
                listOfProdutcts[position].quantidade = (++qte).toDouble()
                //itemBinding.imgInclui.setImageResource(R.drawable.ic_add_circle_24_azul) //setImageResource(R.drawable.ic_menu_camera)
            }
            itemClickList.onImgClick(listOfProdutcts[position]) //passa a lista de produtos ao clicar na imagem

            notifyDataSetChanged() //redesenha a reciclerView
        }
        //================= decrementa ==============================
        itemBinding.imgDelete.setOnClickListener {
            val position =
                holder.bindingAdapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener //se não for, retorne
            qte = listOfProdutcts[position].quantidade //recebe o valor do banco de dados
            if (listOfProdutcts[position].include_item == true) {
                if (qte > 0) {
                    listOfProdutcts[position].quantidade = (--qte).toDouble()
                    Log.d("produto", "origem qte: $qte")
                }

            }
            itemClickList.onImgClick(listOfProdutcts[position]) //passa a lista de produtos ao clicar na imagem
            notifyDataSetChanged() //redesenha a reciclerView
        }
        //===============================================
        return holder
        //  return ListaCompletaViewHolder(itemBinding, parent.context)
    }

    /** cria o holder (cardView)*/
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is ListaCompletaViewHolder -> holder.bind(listOfProdutcts[position])
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
            binding.txtPreco1.text = item.photo_url
            binding.txtPreco1.text = item.valor_s1.toString()
            binding.txtPreco2.text = item.valor_s2.toString()
            binding.txtPreco3.text = item.valor_s3.toString()
            binding.quantidade.text = item.quantidade.toString()
            Log.d("produto", "QTE: ${item.quantidade}")
//            Log.d("resultado", " item.include_item: ${item.include_item} ")
//            Log.d("resultado", "url:  ${item.descricao}")
            // item.quantidade = qte.toDouble()
            if (item.quantidade > 0) {
                binding.imgInclui.setImageResource(R.drawable.ic_add_circle_24_azul)
            } else {
                binding.imgInclui.setImageResource(R.drawable.ic_add_circle_24_verde)
            }

            //  itemView.img_inclui_delete.setOnClickListener {itemClickList.onImgClick(produto = Produto())}

        }

    }

}