package com.example.ecommercestore.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommercestore.R
import com.example.ecommercestore.activities.ShoppingActivity
import com.example.ecommercestore.databinding.FragmentIntroductionBinding
import com.example.ecommercestore.viewmodels.IntroductionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment:Fragment(){
    lateinit var binding:FragmentIntroductionBinding

    val introductionViewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // observe to introduction state
        observeToNavigateState()

        binding.buttonStart.setOnClickListener({
            findNavController()
                .navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        })

    }

    private fun observeToNavigateState() {
        lifecycleScope.launchWhenStarted {
            introductionViewModel.navigate.collect{
                when(it){
                    IntroductionViewModel.SHOPPING_ACTIVITY ->{
                        Intent(requireActivity(),ShoppingActivity::class.java).also {
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(it)
                        }
                    }
                    IntroductionViewModel.ACCOUNT_OPTION_FRAGMENT ->{
                        findNavController()
                            .navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
                    }
                }
            }
        }
    }
}