package com.paviotti.s2.ui.listas.supermercados

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.paviotti.s2.R
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado
import com.paviotti.s2.data.model.User
import com.paviotti.s2.data.remote.supermercados.ListaSupermercadoDataSource
import com.paviotti.s2.data.remote.supermercados.ListaSupermercadoDataSource.Companion.qteSelect
import com.paviotti.s2.databinding.FragmentSupermercadoBinding
import com.paviotti.s2.domain.supermercados.ListaSupermercadoRepositoryImplement
import com.paviotti.s2.presentation.supermercados.ClickListSupermercados
import com.paviotti.s2.presentation.supermercados.ListaSupermercadosViewModel
import com.paviotti.s2.presentation.supermercados.ListaSupermercadosViewModelFactory
import com.paviotti.s2.ui.adapter.ListaSupermercadosAdapter

class SupermercadoFragment : Fragment(R.layout.fragment_supermercado), ClickListSupermercados {
    var qteSelected=0

    private lateinit var binding: FragmentSupermercadoBinding
    private val viewModel by viewModels<ListaSupermercadosViewModel> {
        ListaSupermercadosViewModelFactory(
            ListaSupermercadoRepositoryImplement(
                ListaSupermercadoDataSource()
            )
        )
    }

    //recebe o id do supermecado selecioando
//    companion object {
//        var qteSelect = 0
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSupermercadoBinding.bind(view)
        fetchLatestList()
    }

    //faz a busca na lista do Firebase
    fun fetchLatestList() {
        viewModel.fetchLatestList().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvListasSupermercados.adapter =
                        ListaSupermercadosAdapter(result.data, this@SupermercadoFragment)
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

    fun updateItem(supermercado: Supermercado) {
        val alertDialog =
            AlertDialog.Builder(requireContext()).setTitle("Atualizando Supermercado").create()
        viewModel.updateItem(supermercado).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Loading -> {
                    alertDialog.show()
                }
                is Result.Success -> {
                    alertDialog.dismiss()
                }
                is Result.Failure -> {
                    alertDialog.dismiss()
                }
            }
        })
    }

    //acrescenta
    override fun onUnChkImgClick(supermercado: Supermercado) {
        if (qteSelected <= 3 ) {
            ListaSupermercadosAdapter.gravar = true
            updateItem(supermercado)
            fetchLatestList()
            Log.d("aqui", "aqui: ${qteSelect}")
        } else {
            Toast.makeText(context, "O máximo são 3 supermercados", Toast.LENGTH_SHORT).show()
        }
    }

    //diminui
    override fun onChkImgClick(supermercado: Supermercado) {
            if (qteSelected >=0) {
            ListaSupermercadosAdapter.gravar = false
            updateItem(supermercado)
            fetchLatestList()
            Log.d("aqui", "aqui: ${qteSelect}")
        } else {
            Toast.makeText(context, "O máximo são 3 supermercados", Toast.LENGTH_SHORT).show()
        }
    }
}