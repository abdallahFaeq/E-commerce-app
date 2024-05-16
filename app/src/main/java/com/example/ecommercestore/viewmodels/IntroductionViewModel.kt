package com.example.ecommercestore.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercestore.R
import com.example.ecommercestore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPrferces:SharedPreferences,
    private val firebaseAuth:FirebaseAuth
):ViewModel() {

    private val _navigate = MutableStateFlow(0)
    val navigate:StateFlow<Int> = _navigate

    companion object{
        const val SHOPPING_ACTIVITY = 10
        val ACCOUNT_OPTION_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment
    }

    init {
        val isButtonClicked = sharedPrferces.getBoolean(Constants.INTRODUCTION_KEY,false)

        var user = firebaseAuth.currentUser
        if (user != null){
            // navigate to shoppingActivity
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        }else if (isButtonClicked){
            // if user == null but isButtonClicked is true -> navigate to AccountOption Fragment
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTION_FRAGMENT)
            }
        }else {
            // if user == null and isButtonClicked is false -> show IntroductionFragment
            Unit
        }
    }

    fun onStartClick(){
        sharedPrferces.edit().putBoolean(Constants.INTRODUCTION_KEY,true).apply()
    }
}