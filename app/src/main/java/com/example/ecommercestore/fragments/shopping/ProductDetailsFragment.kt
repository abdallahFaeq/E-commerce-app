package com.example.ecommercestore.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ecommercestore.R
import com.example.ecommercestore.adapters.ColorsAdapter
import com.example.ecommercestore.adapters.SizesAdapter
import com.example.ecommercestore.adapters.ViewPager2ImagesAdapter
import com.example.ecommercestore.databinding.FragmentProductDetailsBinding
import com.example.ecommercestore.models.CartProduct
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.utils.showAndHideBottomNavigation.hideBottomNavigationView
import com.example.ecommercestore.viewmodels.DetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment :Fragment(){
    private lateinit var binding:FragmentProductDetailsBinding

    private val detailsViewModel by viewModels<DetailsViewModel>()
    private val productArgs by navArgs<ProductDetailsFragmentArgs>()

    private val viewPagerImaagesAdapter by lazy{ ViewPager2ImagesAdapter()}
    private val colorsAdapter by lazy { ColorsAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }

    private var selectedColor:Int?=null
    private var selectedSize:String?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailsBinding.inflate(
            inflater
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // retrieving product by safe args
        val product = productArgs.product

        setupViewPagerImagesAdapter()
        setupColorsAdapter()
        setupSizesAdapter()


        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            product.description?.let {
                tvProductDescription.text = it
            }
            if (product.sizes.isNullOrEmpty()){
                tvProductSize.visibility = View.GONE
            }
            if (product.colors.isNullOrEmpty()){
                tvProductColors.visibility = View.GONE
            }
        }



        product.images?.let {
            viewPagerImaagesAdapter.diff.submitList(it)
        }
        product.colors?.let {
            colorsAdapter.diff.submitList(it)
        }
        product.sizes.let {
            sizesAdapter.diff.submitList(it)
        }

        binding.imageClose.setOnClickListener{
            findNavController().navigateUp()
        }
        colorsAdapter.onItemClick = {
            selectedColor = it
        }
        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        binding.buttonAddToCart.setOnClickListener{
            if (selectedColor == null || selectedSize == null){
                Toast.makeText(requireContext(),"you should choose suitable color and size",Toast.LENGTH_SHORT).show()
            }else{
                detailsViewModel.addUpdateInCart(CartProduct(product,1,selectedColor,selectedSize))
            }
        }
        // listen to our state
        lifecycleScope.launchWhenStarted {
            detailsViewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.buttonAddToCart.revertAnimation()
                    }
                    is Resource.Loading -> {
                        binding.buttonAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                        Toast.makeText(requireContext(),"product was added successfully",Toast.LENGTH_SHORT).show()
                    }
                    is Resource.UnSpecified -> {
                        Unit
                    }
                }
            }
        }
    }

    private fun setupSizesAdapter() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
        }
    }

    private fun setupColorsAdapter() {
        binding.rvColors.apply {
            adapter = colorsAdapter
        }
    }

    private fun setupViewPagerImagesAdapter() {
        binding.viewPagerProductImages.adapter = viewPagerImaagesAdapter
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
    }
}