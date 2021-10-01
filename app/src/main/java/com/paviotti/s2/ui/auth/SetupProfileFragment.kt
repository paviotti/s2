package com.paviotti.s2.ui.auth

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.paviotti.s2.R
import com.paviotti.s2.data.remote.auth.AuthDataSource
import com.paviotti.s2.databinding.FragmentSetupProfileBinding
import com.paviotti.s2.domain.auth.AuthRepositoryImplement
import com.paviotti.s2.presentation.auth.AuthViewModel
import com.paviotti.s2.presentation.auth.AuthViewModelFactory
import java.io.File
import com.paviotti.s2.core.Result

class SetupProfileFragment : Fragment(R.layout.fragment_setup_profile) {
    private lateinit var binding: FragmentSetupProfileBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private val ACTION_IMAGE_CAPTURE = 2
    private var bitmap: Bitmap? = null
    private var uriImagem: Uri? = null

    /** cria o acesso ao viewModel fazendo o caminho até p Firebase*/
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(
            AuthRepositoryImplement(
                AuthDataSource()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupProfileBinding.bind(view)

        //captura a foto da galeria ao clicar no botão
        binding.btnSearchPhoto.setOnClickListener {
            try {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
                startActivityForResult(
                    Intent.createChooser(intent, "Escolha uma imagem"),
                    REQUEST_IMAGE_CAPTURE
                )
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Foto não encontrada", Toast.LENGTH_SHORT).show()
            }
        }


        //cria o perfil do usuario, grava no Firebase
        binding.btnCreateProfile.setOnClickListener {
            if (binding.txtUsername.text.isNullOrEmpty()) {
                binding.txtUsername.error = "Digite o nome"
                return@setOnClickListener
            } else {
                val username = binding.txtUsername.text.toString().trim()
                val alertDialog =
                    AlertDialog.Builder(requireContext()).setTitle("Uploding photo...").create()
                //se bitmap não for nulo e username não for vazio
                bitmap?.let {
                    if (username.isNotEmpty()) {
                        viewModel.updateUserProfile(imageBitmap = it, username = username)
                            .observe(viewLifecycleOwner, { result ->
                                /** vai para o servidor*/
                                when (result) {
                                    is Result.Loading -> {
                                        alertDialog.show()
                                    }
                                    is Result.Success -> {
                                        alertDialog.dismiss()
                                        // findNavController().navigate()
                                    }
                                    is Result.Failure -> {
                                        alertDialog.dismiss()
                                    }
                                }
                            })
                    }
                }
            }

        }
    }

    //click na imagem para tirar a foto - é usado até a versão 9
    fun getPhoto9() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val authorize = "com.paviotti.s2" //pega no fileprovider do manifest
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) //local armazenado
            val nameImage = directory.path + "/Salin" + System.currentTimeMillis() + ".jpg"
            val file = File(nameImage)
            uriImagem = FileProvider.getUriForFile(requireContext(), authorize, file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagem)
            startActivityForResult(intent, ACTION_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Deu errado", Toast.LENGTH_SHORT).show()
        }
    }

    //salva a foto capturada da galeria
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //acesso a galeria
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //exibe a imagem na tela em Uri, depois convert para bitmap para subir para o Storage
            var uri = data?.data!!
            uri.let {
                binding.profileImage.setImageURI(uri)
                bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                //https://stackoverflow.com/questions/3750903/how-can-getcontentresolver-be-called-in-android
            }
        }
    }
}