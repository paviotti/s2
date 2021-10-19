package com.paviotti.s2.ui.listas.supermercados

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.paviotti.s2.R
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.model.Supermercado
import com.paviotti.s2.data.remote.supermercados.ListaSupermercadoDataSource
import com.paviotti.s2.databinding.FragmentSupermercadoBinding
import com.paviotti.s2.domain.supermercados.ListaSupermercadoRepositoryImplement
import com.paviotti.s2.presentation.supermercados.ClickListSupermercados
import com.paviotti.s2.presentation.supermercados.ListaSupermercadosViewModel
import com.paviotti.s2.presentation.supermercados.ListaSupermercadosViewModelFactory
import com.paviotti.s2.ui.adapter.ListaSupermercadosAdapter

class SupermercadoFragment : Fragment(R.layout.fragment_supermercado), ClickListSupermercados {
    private lateinit var binding: FragmentSupermercadoBinding
    private val viewModel by viewModels<ListaSupermercadosViewModel> {
        ListaSupermercadosViewModelFactory(
            ListaSupermercadoRepositoryImplement(
                ListaSupermercadoDataSource()
            )
        )
    }

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

    override fun onImgClick(supermercado: Supermercado) {
        Toast.makeText(context, "Clicou no supermercado", Toast.LENGTH_SHORT).show()
    }
}