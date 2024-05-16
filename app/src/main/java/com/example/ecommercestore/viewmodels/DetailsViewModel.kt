package com.example.ecommercestore.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercestore.firebase.FirebaseCommon
import com.example.ecommercestore.models.CartProduct
import com.example.ecommercestore.utils.Constants
import com.example.ecommercestore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth:FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) :ViewModel(){
    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.UnSpecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateInCart(cartProduct: CartProduct){
        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }

        firestore.collection(Constants.USER_COLLECTION)
            .document(auth.uid!!)
            .collection("Cart")
            .whereEqualTo("product.id",cartProduct.product.id)
            .get()
            .addOnSuccessListener {
                it.documents?.let {
                    if (it.isEmpty()){//add a new product
                        Log.e("error details","is empty")
                        addNewProduct(cartProduct)
                    }else{
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct){ // increase quantity
                            Log.e("error details","increase")
                            val documentRef = it.first().id
                            increaseQuantity(documentRef,cartProduct)
                        }else{ // add a new product
                            Log.e("error details","add new product")
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){cartProduct, exception ->
            viewModelScope.launch {
                if (exception == null){
                    _addToCart.emit(Resource.Success(cartProduct!!))
                }else{
                    _addToCart.emit(Resource.Error(exception.message.toString()))
                }
            }

        }
    }

    fun increaseQuantity(documentId:String,cartproduct:CartProduct){
        firebaseCommon.increaseQuantity(documentId){_,e->
                viewModelScope.launch {
                    if (e == null) {
                        _addToCart.emit(Resource.Success(cartproduct))
                    } else {
                        _addToCart.emit(Resource.Error(e.message.toString()))
                    }
                }
        }
    }
}