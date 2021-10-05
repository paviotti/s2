package com.paviotti.s2.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.paviotti.s2.R
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.remote.auth.AuthDataSource
import com.paviotti.s2.databinding.FragmentRegisterBinding
import com.paviotti.s2.domain.auth.AuthRepositoryImplement
import com.paviotti.s2.presentation.auth.AuthViewModel
import com.paviotti.s2.presentation.auth.AuthViewModelFactory

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding

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
        binding = FragmentRegisterBinding.bind(view)
        SignUp()
    }

    //pegar as informações da tela
    private fun SignUp() {
        binding.btnSignUp.setOnClickListener {
            val userName = binding.editTextUsername.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

            //verifica se existe campos em branco
            when {
                password != confirmPassword -> {
                    binding.editTextPassword.error = "As senhas são diferentes"
                    binding.editTextConfirmPassword.error = "As senhas são diferentes"
                    return@setOnClickListener //retorna para digitar
                }
                userName.isEmpty() -> {
                    binding.editTextUsername.error = "Usuário vazio"
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    binding.editTextEmail.error = "Email vazio"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.editTextPassword.error = "Senha vazia"
                    return@setOnClickListener
                }
                confirmPassword.isEmpty() -> {
                    binding.editTextConfirmPassword.error =
                        "contra senha vazia"
                    return@setOnClickListener
                }
                else -> {
               //     Log.d("SignUp", "dados: $userName, $password, $confirmPassword, $email")

                    /**chama a função abaixo passando os dados*/
                    createUser(email, password, userName)
                }
            }
        }
    }
    /**
     * LoginFragment passa (email e senha) digitados para viewModel através de sigIn
     * viewModel passa os dados para interface AuthRepository através de repository.signIn
     * interface AuthRepository compartilha com sua implementação AuthRepositoryImplement
     * AuthRepositoryImplement passa para AuthDataSource através de dataSource.signIn
     * AuthDataSource passa para Firebase o email e a senha
     * Firebase retorna um user como resultado (Result)
     * Result.Loading, Result.Success, Result.Failure (com.paviotti.s2.core.result.kt)
     * Se tudo der certo, o usuário vai para a tela de listas (Result.Success)
     * */

    //passa os dados da tela para a viewModel para criar o usuario
    private fun createUser(email: String, password: String, userName: String) {
        viewModel.signUp(email, password, userName).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignUp.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_registerFragment_to_nav_listas)
                }
                is Result.Failure -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignUp.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        "Erro: ${result.exception}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        })
    }
}