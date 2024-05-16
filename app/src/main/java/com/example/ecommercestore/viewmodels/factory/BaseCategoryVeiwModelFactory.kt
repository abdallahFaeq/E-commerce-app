package com.example.ecommercestore.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecommercestore.models.Category
import com.example.ecommercestore.viewmodels.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryVeiwModelFactory(
    private val firestore:FirebaseFirestore,
    private val category: Category
):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)){
            return CategoryViewModel(firestore,category) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}