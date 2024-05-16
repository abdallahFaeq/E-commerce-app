package com.example.ecommercestore.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecommercestore.R
import com.example.ecommercestore.databinding.ActivityShoppingBinding
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.viewmodels.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    private val cartVM by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launchWhenStarted {
            cartVM.cartProducts.collectLatest {
                when(it){
                   is Resource.Success ->{
                        val count = it.data?.size?:0
                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_blue)
                        }
                   }
                   else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val navController = findNavController(R.id.fragmentContainerViewShopping)
        binding.bottomNavigationView.setupWithNavController(navController)


    }
}

/*
* firebase storage -> save media like product image and how to use that with firestore and will use kotlin coroutine with firebase
* we are building an entire app this app will help us to add products and save them into our database which is firestore
* */