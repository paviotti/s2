package com.paviotti.s2.ui.listas.listas_de_listas

import android.app.AlertDialog

import android.os.Bundle
import android.util.Log

import android.view.View

import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.paviotti.s2.R
import com.paviotti.s2.core.Result

import com.paviotti.s2.data.remote.ListaDeListas.ListaDeListasDataSource
import com.paviotti.s2.databinding.FragmentListaDeListasBinding
import com.paviotti.s2.domain.listadelistas.ListaDeListasRepositoryImplement
import com.paviotti.s2.presentation.listadelistas.ClickList
import com.paviotti.s2.presentation.listadelistas.ListaDeListasViewModel
import com.paviotti.s2.presentation.listadelistas.ListaDeListasViewModelFactory
import com.paviotti.s2.ui.adapter.ListaDeListasAdapter

//adiconado a interface ClickList
class ListaDeListasFragment : Fragment(R.layout.fragment_lista_de_listas), ClickList {
    private lateinit var binding: FragmentListaDeListasBinding
    private val viewModel by viewModels<ListaDeListasViewModel> {
        ListaDeListasViewModelFactory(
            ListaDeListasRepositoryImplement(
                ListaDeListasDataSource()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListaDeListasBinding.bind(view)
        fetchLatestList()
//        val postList = listOf(ListaDeListas("", "Compras 1"),
//            ListaDeListas("", "Compras2"))
//        binding.rvListasDeListas.adapter = ListaDeListasAdapter(postList)

    }

    //https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/26519106#announcements
    private fun createNewItemList() {
        val newItem = binding.txtNewItem.text.toString().trim()
        val alertDialog =
            AlertDialog.Builder(requireContext()).setTitle("Uploding photo...").create()
        //verifica se newItem é null
        newItem.let {
            viewModel.createNewItem(it).observe(viewLifecycleOwner, { result ->
                //  Log.d("createNewItemList", "newItem: $it")
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        alertDialog.show()
                    }
                    is Result.Success -> {

                        binding.progressBar.visibility = View.GONE
                        alertDialog.dismiss()
                    }
                    is Result.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        alertDialog.dismiss()
                    }
                }
            })
        }

    }
    //faz a busca na lista do Firebase
    fun fetchLatestList(){
        viewModel.fetchLatestList().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvListasDeListas.adapter = ListaDeListasAdapter(
                        result.data,
                        this@ListaDeListasFragment
                    )//precisa passar o próprio contexto this@ListaDeListasFragment - aula 56
                    //https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/23919874#announcements
                    // Toast.makeText(requireContext(), "Acessou", Toast.LENGTH_SHORT).show()
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
        binding.btnNewItem.setOnClickListener {
            if (binding.txtNewItem.text.isNullOrEmpty()) {
                binding.txtNewItem.error = "Digite o nome da lista"
            } else {
                createNewItemList()
                binding.txtNewItem.setText("")
                fetchLatestList() //refresh
            }
        }
    }
    //https://www.youtube.com/watch?v=eaMj60Lb05Q&t=1319s
    override fun onItemClick(nomeDaLista: String) {
        val action = ListaDeListasFragmentDirections.actionNavListasToListaCompletaFragment(nomeDaLista)
        findNavController().navigate(action)
       // Log.d("Var", "nome da lista: $nomeDaLista")
    }

    override fun onImageclick(btn_delete: Boolean) {
//        Toast.makeText(context, "Click na imagem $btn_delete", Toast.LENGTH_LONG).show()
//        Log.d("Imagem", "Click imagem")
    }
}