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
import com.paviotti.s2.data.model.VarStatic.Companion.gravar
import com.paviotti.s2.data.model.VarStatic.Companion.qteSupSelect
import com.paviotti.s2.data.remote.supermercados.ListaSupermercadoDataSource
import com.paviotti.s2.databinding.FragmentSupermercadoBinding
import com.paviotti.s2.domain.supermercados.ListaSupermercadoRepositoryImplement
import com.paviotti.s2.presentation.supermercados.ClickListSupermercados
import com.paviotti.s2.presentation.supermercados.ListaSupermercadosViewModel
import com.paviotti.s2.presentation.supermercados.ListaSupermercadosViewModelFactory
import com.paviotti.s2.ui.adapter.ListaSupermercadosAdapter

/**classe responsável pelo código do fragment do supermercados*/
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
        getLatestListaSupermercado()
    }

    /**faz a busca na lista do Firebase*/
    fun getLatestListaSupermercado() {
        viewModel.getLatestListaSupermercado().observe(viewLifecycleOwner, Observer { result ->
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

    /**Recebe o click no checkbox não selecionado e insere no banco em user(total_sup_selec) */
    override fun onUnChkImgClick(supermercado: Supermercado) {
        // getLatestListaSupermercado()
        //  Log.d("aqui", "aquiUn: ${qteSelect}")
        Log.d("aqui", "aqui: ${qteSupSelect}")

        if (qteSupSelect <= 2) {
            gravar = true
            updateItem(supermercado)
        //    Log.d("aqui", "aqui2: ${qteSupSelect}")
        } else {
            Toast.makeText(context, "O máximo são 3 supermercados", Toast.LENGTH_SHORT).show()
        }
        getLatestListaSupermercado()
    }

    /**Recebe o click no checkbox selecionado e remove no banco em user(total_sup_selec) */
    override fun onChkImgClick(supermercado: Supermercado) {
        //  getLatestListaSupermercado()
       // Log.d("aqui", "aqui: ${qteSupSelect}")
        if (qteSupSelect >= 0) {
            gravar = false
            updateItem(supermercado)

            //    Log.d("aqui", "aqui: ${qteSelect}")
        } else {
            Toast.makeText(context, "O máximo são 3 supermercados", Toast.LENGTH_SHORT).show()
        }
        getLatestListaSupermercado()
    }

    /** insere ou exclui a seleção do supermercado da lista */
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
}