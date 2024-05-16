package com.example.ecommercestore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercestore.firebase.FirebaseCommon
import com.example.ecommercestore.helper.getProductPrice
import com.example.ecommercestore.models.CartProduct
import com.example.ecommercestore.utils.Constants
import com.example.ecommercestore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) :ViewModel(){
    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.UnSpecified())
    val cartProducts = _cartProducts.asStateFlow()

    private var cartProductDocuments = mutableListOf<DocumentSnapshot>()

    private var _deleteItem = MutableSharedFlow<CartProduct>()
    val deleteItem = _deleteItem.asSharedFlow()

    val productPrice = cartProducts.map {
         when(it){
             is Resource.Success ->{
                 calculatePrice(it.data!!)
             }
             else -> null
         }
    }

    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble {cartProduct->
            (cartProduct.product.offerPercentage!!.getProductPrice(cartProduct.product.price)*cartProduct.quantity).toDouble()
        }.toFloat()
    }

    init {
        getCartProducts()
    }

    // to retrieve all cartProducts
    private fun getCartProducts(){
        firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!)
            .collection(Constants.CART_COLLECTION)
            .addSnapshotListener{ value,error->
                // addSnapShot like callback will get cartProdct whenever change order
                if (value == null || error!=null){
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error?.message.toString()))
                    }
                }else{
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    cartProductDocuments= value.documents
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(cartProducts))
                    }
                }
            }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChange: FirebaseCommon.QuantityChange
    ){
        val index = _cartProducts.value.data?.indexOf(cartProduct)

        if (index != -1 && index != null){
            val documentId = cartProductDocuments[index].id
            when(quantityChange){
                FirebaseCommon.QuantityChange.INCREASE ->{
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChange.DECREASE ->{
                    if (cartProduct.quantity == 1){
                        // show dialog if you want to delete this item from cart or not
                        viewModelScope.launch {
                            _deleteItem.emit(cartProduct)
                        }
                        return
                    }
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){documentId,error->
            if (error != null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(error.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){documentId,error->
            if (error!= null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(error.message.toString()))
                }
            }
        }
    }

    fun deleteItemFromCart(cartProduct: CartProduct) {
        val index = _cartProducts.value.data?.indexOf(cartProduct)

        if (index != -1 && index != null){
            val documentId = cartProductDocuments[index].id


            firestore.collection(Constants.USER_COLLECTION)
            .document(auth.uid!!)
            .collection(Constants.CART_COLLECTION)
            .document(documentId).delete()
            }
    }
}