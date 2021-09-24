package com.paviotti.s2.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.paviotti.s2.core.Result
import com.paviotti.s2.domain.auth.AuthRepository
import kotlinx.coroutines.Dispatchers

//recebe um repository: AuthRepository
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    fun signIn(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repository.signIn(email, password))) //passa o método signIn acima
        } catch (e: Exception) {
            emit(Result.Failure(e)) //passa um erro
        }
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T // esta é uma nova maneira de chamar
    }
}