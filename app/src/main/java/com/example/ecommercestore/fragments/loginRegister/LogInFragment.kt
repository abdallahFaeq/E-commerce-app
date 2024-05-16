package com.example.ecommercestore.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommercestore.R
import com.example.ecommercestore.activities.ShoppingActivity
import com.example.ecommercestore.databinding.FragmentLogInBinding
import com.example.ecommercestore.dialoge.setupBottomSheetDialoge
import com.example.ecommercestore.utils.RegisterValidation
import com.example.ecommercestore.utils.Resource
import com.example.ecommercestore.viewmodels.LogInViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LogInFragment:Fragment(){
    lateinit var binding:FragmentLogInBinding
    val loginViewModel : LogInViewModel by viewModels()
    val TAG = "LogInFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonLoginLogin.setOnClickListener {
                loginViewModel.signInWithEmailAndPassword(
                    edEmailLogin.text.toString(),
                    edPasswordLogin.text.toString()
                )
            }
            tvDontHaveAccount.setOnClickListener{
                findNavController().navigate(R.id.action_logInFragment_to_registerFragment)
            }

            tvForgotPasswordLogin.setOnClickListener{
                setupBottomSheetDialoge {email->
                    loginViewModel.resetPassword(email)
                }
            }
        }
        observeToLogin()
    }

    private fun observeToLogin() {
        binding.apply {
            lifecycleScope.launchWhenStarted {
                loginViewModel.login.collect{
                    when(it){
                        is Resource.Error -> {
                            Toast.makeText(requireActivity(),it.message,Toast.LENGTH_LONG).show()
                            buttonLoginLogin.revertAnimation()
                        }
                        is Resource.Loading -> {
                            buttonLoginLogin.startAnimation()
                        }
                        is Resource.Success -> {
                            Log.e(TAG,it.data.toString())
                            buttonLoginLogin.revertAnimation()
                            Intent(requireActivity(),ShoppingActivity::class.java).also {intent->
                                // LIKE FINISH
                                intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                          Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                        is Resource.UnSpecified -> {
                            Unit
                        }
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                loginViewModel.validation.collect{
                    if (it.email is RegisterValidation.Failed){
                        binding.edEmailLogin.apply {
                            requestFocus()
                            error = it.email.message
                        }
                    }
                    if (it.password is RegisterValidation.Failed){
                        binding.edPasswordLogin.apply {
                            requestFocus()
                            error = it.password.message
                        }
                    }
                }
            }
        }


        // when forgot password
        lifecycleScope.launchWhenStarted {
            loginViewModel.resetPassword.collect{
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),"Error ${it.message}",Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(),"Reset password was sent to your email",Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.UnSpecified -> {

                    }
                }
            }
        }
    }
}