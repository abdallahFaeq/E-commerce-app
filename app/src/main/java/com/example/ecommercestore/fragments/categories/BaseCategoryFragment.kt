package com.example.ecommercestore.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercestore.R
import com.example.ecommercestore.adapters.BestDealsAdapter
import com.example.ecommercestore.adapters.BestProductsAdapter
import com.example.ecommercestore.databinding.FragmentBaseCategoryBinding

open class BaseCategoryFragment:Fragment() {
    private lateinit var binding:FragmentBaseCategoryBinding

    // will initialize when we call it firs time only
    protected  val bestProducts by lazy { BestProductsAdapter() }
    protected  val bestDealsAdapter by lazy { BestDealsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBestProductsRv()
        setupBestDealsRv()

        // detect if rv is scroll horizontally or vertically
        binding.rvOfferProducts.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx!=0){
                    onOfferProductsPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            // go to bottom of nestedScrollView
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                // arrived to bottom of nestedScrollView and fetch new data from firestore
                onBestProductsPagingRequest()
            }
        })


        // navigate to productDetailsFragment
        bestDealsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }
        bestProducts.onClick = {
            val bundle = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }
    }

    open fun onOfferProductsPagingRequest(){

    }

    open fun onBestProductsPagingRequest(){

    }


    fun hideOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.GONE
   }
    fun showOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.VISIBLE
    }
    fun hideBestLoading(){
        binding.bestProductsProgressBar.visibility = View.GONE
    }
    fun showBestLoading(){
        binding.bestProductsProgressBar.visibility = View.VISIBLE
    }

    private fun setupBestDealsRv() {
        binding.rvOfferProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = bestDealsAdapter
        }
    }

    private fun setupBestProductsRv() {
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProducts
        }
    }
}