package com.paviotti.s2.ui.scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.paviotti.s2.R
import com.paviotti.s2.databinding.FragmentSuccessScanBinding

class SuccessScanFragment : Fragment() {
    private var _binding: FragmentSuccessScanBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    //https://www.udemy.com/course/curso-definitivo-para-aprender-a-programar-en-android/learn/lecture/23770108#questions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSuccessScanBinding.inflate(inflater, container, false)
        val safeArgs: SuccessScanFragmentArgs by navArgs() // é a instancia do segundo fragment + Args
        val code =
            safeArgs.code //recebe o valor do primeiro fragment, é definido em nav_graph arguments

        val key = code.substring(0..43)
        //exibe o valor recebido do primeiro Fragment,mas só os primeiros 44 caracteres
        binding.fragmentSuccessTextViewCode.text = key

        binding.fragmentSuccessButtonBackToScanner.setOnClickListener {
            findNavController().navigateUp() //botão voltar ao scanner
        }

        binding.fragmentSuccessButtonSair.setOnClickListener {
            //precisa definir um caminho para este botão
            findNavController().navigate(R.id.action_successScanFragment_to_nav_listas)
        }

        val root: View = binding.root
        return root
    }

}