package com.example.ecommercestore.fragments.shopping

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercestore.R
import com.example.ecommercestore.adapters.AddressAdapter
import com.example.ecommercestore.adapters.BillingAdapter
import com.example.ecommercestore.databinding.FragmentBillingBinding
import com.example.ecommercestore.models.Address
import com.example.ecommercestore.models.CartProduct
import com.example.ecommercestore.models.order.Order
import com.example.ecommercestore.models.order.OrderStatus
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.viewmodels.BillingViewModel
import com.example.ecommercestore.viewmodels.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment:Fragment() {
    private lateinit var binding:FragmentBillingBinding

    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductAdapter by lazy { BillingAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()

    private val args by navArgs<BillingFragmentArgs>()

    private var totalPrice = 0f
    private var products = emptyList<CartProduct>()

    private var selectedAddress:Address? =null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        totalPrice = args.totalPrice
        products = args.billingProduct.toList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAddressAdapter()
        setupBillingAdapter()



        addressAdapter.onItemClick = {
            selectedAddress = it
            val b = Bundle().apply { putParcelable("address",selectedAddress) }
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment,b)


        }

        addressAdapter.onLongClick = {
            // delete an address from list

        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.addresses.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(),"error ${it.message}",Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is Resource.UnSpecified -> {
                        Unit
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(),"error ${it.message}",Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Snackbar.make(requireView(),"your orders was palced",Snackbar.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    is Resource.UnSpecified -> {
                        Unit
                    }
                }
            }
        }

        binding.tvTotalPrice.text = totalPrice.toString()
        billingProductAdapter.diff.submitList(products)


        if (!(billingProductAdapter.diff.currentList.size>0)){
            binding.apply {
                tvTotalPrice.visibility = View.GONE
                totalBoxContainer.visibility = View.GONE
                middleLine.visibility = View.GONE
                bottomLine.visibility = View.GONE
            }
        }

        binding.imageAddAddress.setOnClickListener{
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        binding.buttonPlaceOrder.setOnClickListener{
            if (selectedAddress == null){
                Toast.makeText(requireContext(),"please select an address",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (billingProductAdapter.diff.currentList.size > 0){
                showConfirmationDialog()
                return@setOnClickListener
            }
            Toast.makeText(requireContext(),"you have not any products to shipped it .. please add products firstly",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmationDialog() {
        // show dialoge
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
                .setMessage("Do you want to order your cart items")
                .setNegativeButton("Cancel"){dialoge,_ ->
                    dialoge.dismiss()
                }
                .setPositiveButton("Ok"){dialoge,_ ->

                    val order = Order(
                        OrderStatus.Ordered.status,
                        totalPrice,
                        products,
                        selectedAddress!!
                    )

                    orderViewModel.placeOrder(order)
                    dialoge.dismiss()
                }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setupBillingAdapter() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = billingProductAdapter
        }
    }

    private fun setupAddressAdapter() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = addressAdapter
        }
    }
}