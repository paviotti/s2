package com.paviotti.s2.ui.scanner

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

/**documentação
 * https://developers.google.com/ml-kit/vision/barcode-scanning/android#3.-get-an-instance-of-barcodescanner*/

class BarcodeAnalyzer(private val barcodeListener: BarcodeListener) : ImageAnalysis.Analyzer {

    //usar esta opção para ler apenas QrCode, para ler todos usar .getClent()
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE
        )
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Passe a imagem para o scanner e faça seu trabalho
            scanner.process(image).addOnSuccessListener { barcodes ->
                //tarefa completada com suceso
                for (barcode in barcodes) {
                    barcodeListener(barcode.rawValue ?: "")
                }

            }
                .addOnFailureListener {
                    //aqui deveria ser tratado as excessões
                }
                .addOnCompleteListener {
                    // é importante fechar a imageProxi
                    imageProxy.close()
                }
        }
    }


}
