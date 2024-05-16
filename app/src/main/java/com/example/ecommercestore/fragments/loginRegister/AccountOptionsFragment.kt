package com.example.ecommercestore.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecommercestore.R
import com.example.ecommercestore.databinding.FragmentAccountOptionsBinding

class AccountOptionsFragment:Fragment(){
    lateinit var binding:FragmentAccountOptionsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            buttonRegisterAccountOptions.setOnClickListener({
                findNavController()
                    .navigate(R.id.action_accountOptionsFragment_to_registerFragment)
            })
            buttonLoginAccountOptions.setOnClickListener({
                findNavController()
                    .navigate(R.id.action_accountOptionsFragment_to_logInFragment)
            })
        }
    }
}