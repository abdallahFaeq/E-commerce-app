package com.example.ecommercestore.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercestore.R
import com.example.ecommercestore.adapters.CartProductAdapter
import com.example.ecommercestore.databinding.FragmentCartBinding
import com.example.ecommercestore.firebase.FirebaseCommon
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.utils.VerticalItemDecoration
import com.example.ecommercestore.viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

class CartFragment:Fragment() {
    private lateinit var binding : FragmentCartBinding
    private val cartViewModel by activityViewModels<CartViewModel>()
    private val cartAdapter by lazy { CartProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartAdapter()

        cartAdapter.onProductClick = {
            val b = Bundle().apply { putParcelable("product",it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment,b)
        }

        cartAdapter.onPlusClick = {
            cartViewModel.changeQuantity(it,FirebaseCommon.QuantityChange.INCREASE)
        }
        cartAdapter.onMinusClick = {
            cartViewModel.changeQuantity(it,FirebaseCommon.QuantityChange.DECREASE)
        }

        // when you have one item only will call this
        lifecycleScope.launchWhenStarted {
            cartViewModel.deleteItem.collectLatest{cartProduct->
                // show dialoge
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete this item from your cart")
                        .setMessage("Do you want to delete this item from you cart")
                        .setNegativeButton("Cancel"){dialoge,_ ->
                            dialoge.dismiss()
                        }
                        .setPositiveButton("Ok"){dialoge,_ ->
                            cartViewModel.deleteItemFromCart(cartProduct)
                            dialoge.dismiss()
                        }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
        var totalPrice = 0f
        lifecycleScope.launchWhenStarted {
            cartViewModel.productPrice.collectLatest {
                binding.tvTotalPrice.text = "$ ${it}"
                it?.let {
                   totalPrice = it
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            cartViewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Error -> {
                        // hide loading
                        binding.progressbarCart.visibility = View.GONE
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.GONE
                        if (it.data!!.isEmpty()){
                            showLayoutCart()
                        }else{
                            hideLayoutCart()
                        }
                        cartAdapter.diff.submitList(it.data)
                    }
                    is Resource.UnSpecified -> {
                        Unit
                    }
                }
            }
        }

        binding.buttonCheckout.setOnClickListener{
            var action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.diff.currentList.toTypedArray())
            findNavController().navigate(action)
        }
    }

    private fun hideLayoutCart() {
        binding.layoutCarEmpty.visibility = View.GONE
    }

    private fun showLayoutCart() {
        binding.layoutCarEmpty.visibility = View.VISIBLE
    }

    private fun setupCartAdapter() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}