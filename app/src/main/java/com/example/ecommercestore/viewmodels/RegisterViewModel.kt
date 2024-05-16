package com.example.ecommercestore.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ecommercestore.models.User
import com.example.ecommercestore.utils.Constants
import com.example.ecommercestore.utils.RegisterFieldState
import com.example.ecommercestore.utils.RegisterValidation
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.utils.validateEmail
import com.example.ecommercestore.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db : FirebaseFirestore
):ViewModel() {
    private var _register = MutableStateFlow<Resource<User>>(Resource.UnSpecified())
    var register : Flow<Resource<User>> = _register

    private var _validation = Channel<RegisterFieldState>()
    val validation  = _validation.receiveAsFlow()
    fun createAccountByEmailAndPassword(user:User,password:String){
        if (checkValidation(user.email, password)){

            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth
                .createUserWithEmailAndPassword(user.email,password)
                .addOnSuccessListener {
                    it.user?.let {
                        // save data in fireSotore
                        saveUserInfo(it.uid,user)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        }else{
            var registerFieldState = RegisterFieldState(
                validateEmail(user.email),
                validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }

    }

    private fun saveUserInfo(uid: String, user: User) {
        db.collection(Constants.USER_COLLECTION)
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener{
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(email:String, password: String):Boolean{
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        return shouldRegister
    }
}
// module class
// complete di to inject constructor