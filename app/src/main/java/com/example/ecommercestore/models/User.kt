package com.example.ecommercestore.models

data class User (
    val firstName:String,
    val lastName:String,
    val email:String,
    val imagePath:String?=null
){
    constructor():this("","","","")
}