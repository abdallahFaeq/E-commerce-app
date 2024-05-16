package com.example.ecommercestore.utils.showAndHideBottomNavigation

import android.view.View
import androidx.fragment.app.Fragment
import com.example.ecommercestore.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bottomNavigationView?.visibility = View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bottomNavigationView?.visibility = View.VISIBLE
}