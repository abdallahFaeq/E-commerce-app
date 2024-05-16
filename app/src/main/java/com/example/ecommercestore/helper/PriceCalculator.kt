package com.example.ecommercestore.helper

fun Float.getProductPrice(price:Float):Float{
   if (this != null){
       val remainingPercentage = 1f-this
       val priceAfterPercentage = remainingPercentage*price
       return priceAfterPercentage
   }
    return price
}