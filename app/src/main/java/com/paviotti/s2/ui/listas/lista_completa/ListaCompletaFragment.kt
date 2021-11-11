package com.paviotti.s2.ui.listas.lista_completa

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import com.paviotti.s2.R
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Produto
import com.paviotti.s2.data.model.VarStatic.Companion.idList
import com.paviotti.s2.data.model.VarStatic.Companion.nameListFull
import com.paviotti.s2.data.model.VarStatic.Companion.nome_s1
import com.paviotti.s2.data.model.VarStatic.Companion.nome_s2
import com.paviotti.s2.data.model.VarStatic.Companion.nome_s3
import com.paviotti.s2.data.model.VarStatic.Companion.total_s1
import com.paviotti.s2.data.model.VarStatic.Companion.total_s2
import com.paviotti.s2.data.model.VarStatic.Companion.total_s3
import com.paviotti.s2.data.remote.lista_completa.ListaCompletaDataSource
import com.paviotti.s2.databinding.FragmentListaCompletaBinding
import com.paviotti.s2.domain.lista_completa.ListaCompletaRepository
import com.paviotti.s2.domain.lista_completa.ListaCompletaRepositoryImplement
import com.paviotti.s2.presentation.lista_completa.ClickListaCompleta
import com.paviotti.s2.presentation.lista_completa.ListaCompletaViewModel
import com.paviotti.s2.presentation.lista_completa.ListaCompletaViewModelFactory
import com.paviotti.s2.presentation.listadelistas.ListaDeListasViewModel
import com.paviotti.s2.ui.adapter.ListaCompletaAdapter
import com.paviotti.s2.ui.listas.listas_de_listas.ListaDeListasFragmentDirections
import kotlinx.android.synthetic.main.card_lista_completa.*
import java.text.DecimalFormat


class ListaCompletaFragment : Fragment(R.layout.fragment_lista_completa), ClickListaCompleta {

    private lateinit var binding: FragmentListaCompletaBinding
    private val safeArgs: ListaCompletaFragmentArgs by navArgs<ListaCompletaFragmentArgs>() // é a instancia do segundo fragment + Args
    private val viewModel by viewModels<ListaCompletaViewModel> {
        ListaCompletaViewModelFactory(
            ListaCompletaRepositoryImplement(
                ListaCompletaDataSource()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLatestListComplete() //posição ideal
        binding = FragmentListaCompletaBinding.bind(view)
        binding.titulo.text =
            safeArgs.nameList //recebe o valor do primeiro fragment, é definido em nav_graph arguments
        idList = safeArgs.idList
        nameListFull = safeArgs.nameList //pserá passado para dataSource
    }

    fun fetchLatestListComplete() {
        //faz a busca na lista do Firebase
        viewModel.fetchLatestListComplete().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    //binding.titulo.text = viewModel.nomeLista
                    val dec = DecimalFormat("#,###.00")

                    //nome e totais das listas
                    binding.txtP1.text = dec.format(total_s1).toString()
                    binding.txtP2.text = dec.format(total_s2).toString()
                    binding.txtP3.text = dec.format(total_s3).toString()
                    binding.txtS1.text= nome_s1
                    binding.txtS2.text= nome_s2
                    binding.txtS3.text= nome_s3
                    binding.rvListaCompleta.adapter = ListaCompletaAdapter(
                        result.data, this@ListaCompletaFragment
                    )
                }
                is Result.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Ocorreu um erro: ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun creatNewItemList(produto: Produto) {
        produto.let {
            viewModel.createNewItem(it).observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Failure -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            })
        }
    }

    //recebe os valores dos itens do produto selecionado
    override fun onImgClickAdd(produto: Produto) {
        produto.include_item = true //atualiza o campo
        creatNewItemList(produto) //pede para incluir um produto
        updateItemLista(produto) //altera ou exclui
        fetchLatestListComplete() //atualiza a lista de produtos se não ativar não soma
    }

    //atualiza ou exclui um item da nova lista
    override fun onImgClickSub(produto: Produto) {
        updateItemLista(produto) //altera ou exclui
        fetchLatestListComplete()
    }

    //atulaiza a lista ao chegar ao fim da tela
    override fun onClickScroll() {
     //   fetchLatestListComplete()
//        Log.d("roll", "fim da tela")
        Toast.makeText(context, "fim da tela", Toast.LENGTH_LONG).show()
    }

    //atualiza a lista de produtos para somar os valores
    private fun updateSun(produto: Produto) {
//        val alertDialog =
//            AlertDialog.Builder(requireContext()).setTitle("Verificando quantidade ").create()
        produto.let {
            viewModel.updateSun(it).observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                   //     alertDialog.show()
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                   //     alertDialog.dismiss()
                    }
                    is Result.Failure -> {
                        binding.progressBar.visibility = View.GONE
                     //   alertDialog.dismiss()
                    }
                }
            })

        }
    }

    private fun updateItemLista(produto: Produto) {
//        val alertDialog =
//            AlertDialog.Builder(requireContext()).setTitle("Atualizando a quantidade...").create()
        produto.let {
            viewModel.updateItemLista(it).observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.GONE
               //         alertDialog.show()
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                     //   alertDialog.dismiss()
                    }
                    is Result.Failure -> {
                        binding.progressBar.visibility = View.GONE
                    //    alertDialog.dismiss()
                    }
                }
            })
        }
    }
}