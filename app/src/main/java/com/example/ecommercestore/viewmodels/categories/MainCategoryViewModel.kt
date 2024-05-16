package com.example.ecommercestore.viewmodels.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercestore.models.Product
import com.example.ecommercestore.utils.Constants
import com.example.ecommercestore.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore:FirebaseFirestore
): ViewModel() {

    private var _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.UnSpecified())
    val specialProducts:StateFlow<Resource<List<Product>>> = _specialProducts

    private val pagingInfo = PagingInfo(1)

    init {
        fetchSpecialProductsFromFireStore()
    }

    fun fetchSpecialProductsFromFireStore(){

        if (!pagingInfo.isReachEnd){
            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }

            // wherEqualto(category(fieldName),valueOfField like specialProducts)
            firestore.collection(Constants.PRODUCTS)
                .limit(pagingInfo.Page*10)
                .get()
                .addOnSuccessListener {querySnapshots->
                    var productsList = mutableListOf<Product>()
                    val documents = querySnapshots.documents
                    documents.forEach{document->
                        var product = document.toObject(Product::class.java)
                        product?.let {
                            productsList.add(it)
                        }
                    }
                    // detect if pagingList equal to list in firestore or not
                    pagingInfo.isReachEnd = productsList == pagingInfo.bestProductsPaging
                    pagingInfo.bestProductsPaging = productsList

                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(productsList))
                    }
                    pagingInfo.Page++
                }
                .addOnFailureListener{
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}
internal data class PagingInfo(
    var Page:Long,
    var bestProductsPaging:List<Product> = emptyList(),
    var isReachEnd:Boolean = false)


