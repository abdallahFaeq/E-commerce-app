package com.example.ecommercestore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercestore.models.Address
import com.example.ecommercestore.utils.Constants
import com.example.ecommercestore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth:FirebaseAuth
) :ViewModel(){
    private var _addresses = MutableStateFlow<Resource<List<Address>>>(Resource.UnSpecified())
    val addresses = _addresses.asStateFlow()

    init {
        getUserAdresses()
    }

    fun getUserAdresses(){
       viewModelScope.launch {
           _addresses.emit(Resource.Loading())
       }

       firestore.collection(Constants.USER_COLLECTION)
           .document(auth.uid!!)
           .collection(Constants.ADDRESS)
           .addSnapshotListener{value, error ->
               if (error != null){
                   viewModelScope.launch {
                       _addresses.emit(Resource.Error(error.message.toString()))
                   }
                   return@addSnapshotListener
               }
               var addresses = value?.toObjects(Address::class.java)
               viewModelScope.launch {
                   _addresses.emit(Resource.Success(addresses!!))
               }
           }
    }

}