package com.example.ecommercestore.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommercestore.models.Category
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.viewmodels.CategoryViewModel
import com.example.ecommercestore.viewmodels.factory.BaseCategoryVeiwModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TableFragment:BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val categoryViewModel by viewModels<CategoryViewModel> {
        BaseCategoryVeiwModelFactory(firestore, Category.Table)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeToStates()

    }

    // paging
    override fun onOfferProductsPagingRequest() {

    }

    override fun onBestProductsPagingRequest() {

    }

    private fun observeToStates() {
        lifecycleScope.launchWhenStarted {
            categoryViewModel.offerProductsFlow.collectLatest {
                when(it){
                    is Resource.Error -> {
                        hideOfferLoading()
                        Log.e("errors",it.message.toString())                    }
                    is Resource.Loading -> {
                        // show offerProgressBar
                        showOfferLoading()
                    }
                    is Resource.Success -> {
                        hideOfferLoading()
                        bestDealsAdapter.diff.submitList(it.data)
                    }
                    is Resource.UnSpecified -> {
                        Unit
                    }
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            categoryViewModel.bestProductsFlow.collectLatest {
                when(it){
                    is Resource.Error -> {
                        hideBestLoading()
                        Log.e("errors",it.message.toString())                    }
                    is Resource.Loading -> {
                        // show offerProgressBar
                        showBestLoading()
                    }
                    is Resource.Success -> {
                        hideBestLoading()
                        bestProducts.diff.submitList(it.data)
                    }
                    is Resource.UnSpecified -> {
                        Unit
                    }
                }
            }
        }

    }
}