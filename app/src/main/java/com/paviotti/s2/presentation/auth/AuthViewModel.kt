package com.paviotti.s2.presentation.auth


import android.graphics.Bitmap
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

    //inscrever-se, recebe os dados de Register.Fragment.kt
    fun signUp(email: String, password: String, username:String)= liveData(Dispatchers.IO){
        emit(Result.Loading())
        try {
            emit(Result.Success(repository.signUn(email, password, username))) //passa os dados para AuthRepository.kt
        }catch (e:Exception){
            emit(Result.Failure(e))
        }
    }

    /**
     * *atualiza os dados inseridos no Profile, passando a foto e o nome do usuário
     * os dados vem do Fragment_setup_profile*/
    fun updateUserProfile(imageBitmap: Bitmap, username: String)=liveData(Dispatchers.IO){
    emit(Result.Loading())
        try {
            emit(Result.Success(repository.updateProfile (imageBitmap, username)))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T // esta é uma nova maneira de chamar
    }
}