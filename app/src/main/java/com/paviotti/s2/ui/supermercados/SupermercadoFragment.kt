package com.paviotti.s2.ui.supermercados

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.paviotti.s2.databinding.FragmentSupermercadoBinding

class SupermercadoFragment : Fragment() {

    private lateinit var supermercadoViewModel: SupermercadoViewModel
    private var _binding: FragmentSupermercadoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        supermercadoViewModel =
            ViewModelProvider(this).get(SupermercadoViewModel::class.java)

        _binding = FragmentSupermercadoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        supermercadoViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}