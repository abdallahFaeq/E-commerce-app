package com.example.ecommercestore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercestore.models.Category
import com.example.ecommercestore.models.Product
import com.example.ecommercestore.utils.Constants
import com.example.ecommercestore.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel (
    private val firestroe:FirebaseFirestore,
    private val category: Category
) :ViewModel(){

    private var _bestProductsFlow = MutableStateFlow<Resource<List<Product>>>(Resource.UnSpecified())
    val bestProductsFlow = _bestProductsFlow.asStateFlow()

    private var _offerProductsFlow = MutableStateFlow<Resource<List<Product>>>(Resource.UnSpecified())
    val offerProductsFlow = _offerProductsFlow.asStateFlow()

    init {
        fetchBestProducts()
        fetchOfferProducts()
    }

    fun fetchBestProducts(){
        viewModelScope.launch {
            _bestProductsFlow.emit(Resource.Loading())
        }
        firestroe.collection(Constants.PRODUCTS)
            .whereEqualTo("category",category.category)
            .whereEqualTo("offerPercentage",null)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProductsFlow.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _bestProductsFlow.emit(Resource.Error(it.message.toString()))
                }
            }
    }


    fun fetchOfferProducts(){
        viewModelScope.launch {
            _offerProductsFlow.emit(Resource.Loading())
        }
        firestroe.collection(Constants.PRODUCTS)
            .whereEqualTo("category",category.category)
            .whereNotEqualTo("offerPercentage",null)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProductsFlow.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _offerProductsFlow.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}