package com.paviotti.s2.ui.scanner

import android.Manifest.permission.CAMERA
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.paviotti.s2.R
import com.paviotti.s2.databinding.FragmentQrCodeBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

typealias BarcodeListener = (barcode: String) -> Unit

class QrCodeFragment : Fragment() {
    private var processingBarcode = AtomicBoolean(false)
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var qrCodeViewModel: QrCodeViewModel //variavel da viewModel

    private var _binding: FragmentQrCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        //lembrar que class QrCodeViewModel: ViewModel(), precisa extender
        qrCodeViewModel = ViewModelProvider(this).get(QrCodeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //val v = inflater.inflate(R.layout.fragment_qr_code, container, false)
        _binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        qrCodeViewModel.progressState.observe(viewLifecycleOwner, {
            binding.fragmentScanBarcodeProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        })

        qrCodeViewModel.navigation.observe(viewLifecycleOwner, { navDirections ->
            navDirections?.let {
                findNavController().navigate(navDirections)
                qrCodeViewModel.doneNavigating()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onResume() {
        super.onResume()
        processingBarcode.set(false)
    }

    private fun startCamera() {
        //cria uma instancia de ProcessCameraProvider
        val cameraProvideFuture = ProcessCameraProvider.getInstance(requireContext())
        //fica escutando
        cameraProvideFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProvideFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(
                    //androidx.camera.view.PreviewView em fragment_qr_code.xml
                    binding.fragmentScanBarcodePreviewView.surfaceProvider
                )
            }
            //Configure o ImageAnalyzer para o caso de uso ImageAnalysis
            val imageAnalysis = ImageAnalysis.Builder().build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                        if (processingBarcode.compareAndSet(false, true)) {
                            searchBarcode(barcode)
                        }
                    })
                }
            //seleciona camera traseira
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                //Desvincule quaisquer casos de uso vinculados antes de religar
                cameraProvider.unbindAll()
                // Bind use cases to lifecycleOwner
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Log.e("PreviewUseCase", "Binding failed! :(", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissões não concedidas pelo usuário",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //passa o barcode para p/ viewModel
    private fun searchBarcode(barcode: String) {
        qrCodeViewModel.searchBarcode(barcode)
    }

    //desliga a camera antes de sair
    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}