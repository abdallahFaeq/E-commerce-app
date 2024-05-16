package com.example.ecommercestore.firebase

import com.example.ecommercestore.models.CartProduct
import com.example.ecommercestore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class FirebaseCommon (
    private val firestore: FirebaseFirestore,
    val auth:FirebaseAuth
){
    private val cartCollection = firestore.collection(Constants.USER_COLLECTION)
        .document(auth.uid!!).collection(Constants.CART_COLLECTION)

    fun addProductToCart(cartProduct: CartProduct, onResult:(CartProduct?,Exception?)->Unit){
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct,null)
            }
            .addOnFailureListener{
                onResult(null,it)
            }
    }

    fun increaseQuantity(documentId:String,onResult: (String?, Exception?) -> Unit){
        val documentRef = cartCollection.document(documentId)
        firestore.runTransaction{transaction->
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let{cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef,newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentId,null)
            }
            .addOnFailureListener{
                onResult(null,it)
            }
    }

    fun decreaseQuantity(documentId:String,onResult: (String?, Exception?) -> Unit){
        val documentRef = cartCollection.document(documentId)
        firestore.runTransaction{transaction->
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let{cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef,newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentId,null)
            }
            .addOnFailureListener{
                onResult(null,it)
            }
    }
    enum class QuantityChange{
        INCREASE, DECREASE
    }
}