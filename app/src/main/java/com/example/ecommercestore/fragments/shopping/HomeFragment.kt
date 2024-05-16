package com.example.ecommercestore.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecommercestore.R
import com.example.ecommercestore.adapters.HomeViewPagerAdapter
import com.example.ecommercestore.databinding.FragmentHomeBinding
import com.example.ecommercestore.fragments.categories.AccessoryFragment
import com.example.ecommercestore.fragments.categories.BaseCategoryFragment
import com.example.ecommercestore.fragments.categories.ChairFragment
import com.example.ecommercestore.fragments.categories.CupboardFragment
import com.example.ecommercestore.fragments.categories.FornitureFragment
import com.example.ecommercestore.fragments.categories.MainCategoryFragment
import com.example.ecommercestore.fragments.categories.TableFragment
import com.example.ecommercestore.utils.Constants
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


class HomeFragment:Fragment(R.layout.fragment_home){

    private lateinit var binding:FragmentHomeBinding
    val TAG = "HomeFragment"
    lateinit var chairFragment:ChairFragment
    lateinit var cupboardFragment: CupboardFragment
    lateinit var tableFragment: TableFragment
    lateinit var accessoryFragment: AccessoryFragment
    lateinit var fornitureFragment: FornitureFragment

    lateinit var tabLayoutMediator:TabLayoutMediator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chairFragment = ChairFragment()
        cupboardFragment = CupboardFragment()
        tableFragment = TableFragment()
        accessoryFragment = AccessoryFragment()
        fornitureFragment = FornitureFragment()



        // first need list of categories fragments
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            chairFragment,
            cupboardFragment,
            tableFragment,
            accessoryFragment,
            fornitureFragment
        )
        // second need adapter
        val viewPagerAdapter = HomeViewPagerAdapter(
            categoriesFragments,
            childFragmentManager,
            lifecycle
        )
        binding.viewpagerHome.isUserInputEnabled = false
        // third -> setup veiwpager
        binding.viewpagerHome.adapter = viewPagerAdapter
        // four -> communicate tablayout and viewpager about tablayoutMediator
        tabLayoutMediator = TabLayoutMediator(binding.tabLayout,binding.viewpagerHome){tabs,position->
            when(position){
                0 -> {
                    tabs.text = "Main"
                }
                1 ->{
                    tabs.text = "Chair"
                }
                2 -> {
                    tabs.text = "Cupboard"

                }
                3 -> {
                    tabs.text = "Table"
                }
                4 -> {
                    tabs.text = "Accessory"
                }
                5 -> {
                    tabs.text = "Forniture"
                }
            }
        }
        tabLayoutMediator.attach()
    }

    override fun onStart() {
        super.onStart()
//        binding.tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                when(tab?.text){
//                    "Chair" -> {
//                        Log.e(TAG,"chair")
//                        BaseCategoryFragment.categoryValue = Constants.CHAIRS
//                    }
//                    "Cupboard" ->{
//                        Log.e(TAG,"cupboard")
//                        BaseCategoryFragment.categoryValue = Constants.CUPBOARDS
//                    }
//                    "Table"->{
//                        Log.e(TAG,"table")
//                        BaseCategoryFragment.categoryValue = Constants.TABLES
//                    }
//                    "Accessory"->{
//                        Log.e(TAG,"accessory")
//                        BaseCategoryFragment.categoryValue = Constants.ACCESSORIES
//                    }
//                    "Forniture"->{
//                        Log.e(TAG,"forniture")
//                        BaseCategoryFragment.categoryValue = Constants.FORNITURES
//                    }
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//
//            }
//
//        })
    }
}

/*
start with category class which describe category we have in the app
then and now we want to create a viewModel to fetch data from firebase
 */