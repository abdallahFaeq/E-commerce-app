package com.example.ecommercestore.models

import com.example.ecommercestore.utils.Constants

sealed class Category (val category:String){
    object Chair:Category(Constants.CHAIRS)
    object Cupboard:Category(Constants.CUPBOARDS)
    object Table:Category(Constants.TABLES)
    object Accessory:Category(Constants.ACCESSORIES)
    object Forniture:Category(Constants.FORNITURES)
}