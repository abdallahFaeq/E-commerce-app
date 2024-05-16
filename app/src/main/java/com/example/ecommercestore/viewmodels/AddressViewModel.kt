package com.example.ecommercestore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercestore.models.Address
import com.example.ecommercestore.utils.Constants
import com.example.ecommercestore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth:FirebaseAuth
) :ViewModel(){
    private var _addAddress = MutableStateFlow<Resource<Address>>(Resource.UnSpecified())
    val addAddress = _addAddress.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address:Address){
        viewModelScope.launch {
            _addAddress.emit(Resource.Loading())
        }

        val validateInputs = validateInputs(address)
        if (validateInputs){
            firestore.collection(Constants.USER_COLLECTION)
                .document(auth.uid!!)
                .collection(Constants.ADDRESS)
                .document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _addAddress.emit(Resource.Success(address))
                    }
                }
                .addOnFailureListener{
                    viewModelScope.launch {
                        _addAddress.emit(Resource.Error(it.message.toString()))
                    }
                }
        }else{

            viewModelScope.launch {
                _error.emit("All inputs required")
            }
        }

    }

    private fun validateInputs(address:Address): Boolean {
        return  address.addressTitle.isNotEmpty() &&
                address.fullName.isNotEmpty() &&
                address.street.isNotEmpty() &&
                address.phone.isNotEmpty() &&
                address.city.isNotEmpty() &&
                address.state.isNotEmpty()
    }
}