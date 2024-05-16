package com.example.ecommercestore.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercestore.R
import com.example.ecommercestore.adapters.BestDealsAdapter
import com.example.ecommercestore.adapters.BestProductsAdapter
import com.example.ecommercestore.adapters.SpecialProductsAdapter
import com.example.ecommercestore.databinding.FragmentMainCategoryBinding
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.utils.showAndHideBottomNavigation.showBottomNavigationView
import com.example.ecommercestore.viewmodels.categories.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainCategoryFragment:Fragment() {
    private lateinit var binding : FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter:BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    val mainCategoryViewModel by viewModels<MainCategoryViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup specialProductsAdapter
        setupSepcialProductsAdapter()
        setupBestDealsAdapter()
        setupBestProductsAdapter()

        observeToSpecialProducts()



        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v,_,scrollY,_,_ ->
            // go to bottom of nestedScrollView
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                // arrived to bottom of nestedScrollView and fetch new data from firestore
                mainCategoryViewModel.fetchSpecialProductsFromFireStore()
            }
        })
    }

    private fun setupBestProductsAdapter() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }
    }

    private fun setupBestDealsAdapter() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = bestDealsAdapter
        }
    }

    private fun observeToSpecialProducts() {
        lifecycleScope.launchWhenStarted {
            mainCategoryViewModel.specialProducts.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_LONG).show()
                        binding.bestProductsProgressbar.visibility  = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.bestProductsProgressbar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.diff.submitList(it.data)
                        bestDealsAdapter.diff.submitList(it.data)
                        bestProductsAdapter.diff.submitList(it.data)
                        binding.bestProductsProgressbar.visibility = View.GONE
                    }
                    is Resource.UnSpecified -> {
                        Unit
                    }
                }
            }
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility = View.VISIBLE
    }

    private fun setupSepcialProductsAdapter() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = specialProductsAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        specialProductsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }

        bestProductsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }

        bestDealsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }

    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}