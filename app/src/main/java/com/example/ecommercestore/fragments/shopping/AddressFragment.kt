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
import com.example.ecommercestore.databinding.FragmentAddressBinding
import com.example.ecommercestore.models.Address
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.viewmodels.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment:Fragment() {
    private lateinit var binding:FragmentAddressBinding
    private val addressViewModel by viewModels<AddressViewModel>()
    val args by navArgs<AddressFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            addressViewModel.addAddress.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility =View.GONE
                        Toast.makeText(requireContext(),"error ${it.message.toString()}",Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(),"address was added",Toast.LENGTH_SHORT).show()
                    }
                    is Resource.UnSpecified -> {
                        Unit
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            addressViewModel.error.collectLatest {
                if (it.isNotEmpty()){
                    binding.progressbarAddress.visibility = View.GONE
                    Toast.makeText(requireContext(),"error ${it}",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // implement inteface then implemnent functions inside it .. write code
    // adapter.onItemClickListenrer = { }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddressBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = args.address
        if (address==null){
            binding.buttonDelelte.visibility = View.GONE
        }else{
            setViews(address)
        }

        binding.apply {
            buttonSave.setOnClickListener{
                // retrieve inputs
                val addressTitle = edAddressTitle.text.toString().trim()
                val fullName = edFullName.text.toString().trim()
                val street = edStreet.text.toString().trim()
                val phone = edPhone.text.toString().trim()
                val city = edCity.text.toString().trim()
                val state = edState.text.toString().trim()

                val address = Address(
                    addressTitle,
                    fullName,
                    street,
                    phone,
                    city,
                    state
                )
                addressViewModel.addAddress(address)
            }
        }

        binding.imageAddressClose.setOnClickListener{
            findNavController().navigateUp()
        }
    }
    fun setViews(address: Address){
        binding.apply {
            edAddressTitle.setText(address.addressTitle)
            edFullName.setText(address.fullName)
            edStreet.setText(address.street)
            edPhone.setText(address.phone)
            edCity.setText(address.city)
            edState.setText(address.state)
        }
    }
}