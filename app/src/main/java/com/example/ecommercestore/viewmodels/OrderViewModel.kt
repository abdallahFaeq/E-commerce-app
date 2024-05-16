package com.example.ecommercestore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercestore.models.order.Order
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
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth:FirebaseAuth
):ViewModel() {
    private var _order = MutableStateFlow<Resource<Order>>(Resource.UnSpecified())
    val order = _order.asStateFlow()

    fun placeOrder(order:Order){
        //TODO: Add orders into order_collection inside user collection
        //TODO: Add order collection
        //TODO: Deleter orders sub_collection

        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }

        firestore.runBatch {writeBatch->
            firestore.collection(Constants.USER_COLLECTION)
                .document(auth.uid!!)
                .collection(Constants.ORDERS_SUB_COLLECTION)
                .document()
                .set(order)

            firestore.collection(Constants.ORDERS_SUB_COLLECTION)
                .document()
                .set(order)

            firestore.collection(Constants.USER_COLLECTION)
                .document(auth.uid!!)
                .collection(Constants.CART_COLLECTION)
                .get()
                .addOnSuccessListener {
                    it.documents.forEach{
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener{
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}