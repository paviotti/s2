package com.paviotti.s2.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.paviotti.s2.R
import com.paviotti.s2.core.Result
import com.paviotti.s2.data.remote.auth.AuthDataSource
import com.paviotti.s2.databinding.FragmentLoginBinding
import com.paviotti.s2.domain.auth.AuthRepositoryImplement
import com.paviotti.s2.presentation.auth.AuthViewModel
import com.paviotti.s2.presentation.auth.AuthViewModelFactory


class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    //cria uam isntancia do FirebaseAuth e se inicializa no momento que se utiliza (lazy)
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    //cria o viewModel
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(
            AuthRepositoryImplement(
                AuthDataSource()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        //chama as funções
        isUserLoggedIn()
        doLogin()
        goToSignUpPage()
    }

    //verifica se está logado, se estiver, não passa mais pela tela de login, senão vai para a função abaixo, doLogin()
    private fun isUserLoggedIn() {
        firebaseAuth.currentUser?.let {
            findNavController().navigate(R.id.action_loginFragment_to_nav_listas)
        }
    }

    private fun doLogin() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            //chama as funções
            validateCredentials(email, password)
            signIn(email, password)
        }
    }

    //se não estiver logado, vai para a tela de registro
    private fun goToSignUpPage(){
        binding.txtSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun validateCredentials(email: String, password: String) {
        if (email.isEmpty()) {
            binding.editTextEmail.error = "Preencha o e-mail por favor"
            return
        }
        if (password.isEmpty()) {
            binding.editTextPassword.error = "Preencha a senha por favor"
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

    private fun signIn(email: String, password: String) {
        viewModel.signIn(email, password).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignIn.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_loginFragment_to_nav_listas)
                }
                is Result.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignIn.isEnabled = true
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