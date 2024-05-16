package com.example.ecommercestore.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommercestore.R
import com.example.ecommercestore.databinding.FragmentRegisterBinding
import com.example.ecommercestore.models.User
import com.example.ecommercestore.utils.RegisterValidation
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class RegisterFragment:Fragment(){
    val TAG = "RegisterFragment"
    lateinit var binding:FragmentRegisterBinding
    val registerViewModel : RegisterViewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonRegisterRegister.setOnClickListener({
                var user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim(),
                )

                var password = edPasswordRegister.text.toString()

                registerViewModel.createAccountByEmailAndPassword(user,password)
            })
            tvDoYouHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_logInFragment)
            }
        }
        observeToRegister()
    }

    private fun observeToRegister() {
        lifecycleScope.launchWhenStarted {
            registerViewModel.register.collect{
                when(it){
                    is Resource.Error -> {
                        Log.e(TAG,it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.e(TAG,it.data.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            registerViewModel.validation.collect(){validation->
                if (validation.email is RegisterValidation.Failed){
                    binding.edEmailRegister.apply {
                        requestFocus()
                        error = validation.email.message
                    }
                }
                if (validation.password is RegisterValidation.Failed){
                    binding.edPasswordRegister.apply {
                        requestFocus()
                        error = validation.password
                            .message
                    }
                }
            }
        }
    }
}