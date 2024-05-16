package com.example.ecommercestore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercestore.utils.RegisterFieldState
import com.example.ecommercestore.utils.RegisterValidation
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.utils.validateEmail
import com.example.ecommercestore.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    var firebaseAuth:FirebaseAuth
):ViewModel() {
    private var _login = MutableSharedFlow<Resource<FirebaseUser>>()
    var login  = _login.asSharedFlow()

    private var _validation = Channel<RegisterFieldState>()
    var validation = _validation.receiveAsFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun signInWithEmailAndPassword(email:String, password:String){

        // check validation
        if (checkValidation(email,password)){

        runBlocking {
           _login.emit(Resource.Loading())
       }
        firebaseAuth
           .signInWithEmailAndPassword(
               email, password
           )
           .addOnCompleteListener {
               if (it.isSuccessful){
                   viewModelScope.launch {
                       _login.emit(Resource.Success(it.result.user!!))
                   }

               }else{
                   viewModelScope.launch {
                       _login.emit(Resource.Error(it.exception?.localizedMessage!!))
                   }
               }
           }
        }else{
            val loginFieldState = RegisterFieldState(
                validateEmail(email),
                validatePassword(password)
            )

            runBlocking {
                _validation.send(loginFieldState)
            }
        }
    }

    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)

        var shouldLogin = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        return shouldLogin
    }

    fun resetPassword(email:String){
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}