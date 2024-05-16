package com.example.ecommercestore.models.order

sealed class OrderStatus (val status:String){
    object Ordered: OrderStatus("Ordered")
    object Canceled: OrderStatus("Canceled")
    object Shipped: OrderStatus("Shipped")
    object Delivered: OrderStatus("Delievered")
    object Returned: OrderStatus("Returned")
    object Confirmed: OrderStatus("Confirmed")
}

fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Ordered" -> {
            OrderStatus.Ordered
        }
        "Canceled" -> {
            OrderStatus.Canceled
        }
        "Confirmed" -> {
            OrderStatus.Confirmed
        }
        "Shipped" -> {
            OrderStatus.Shipped
        }
        "Delievered" -> {
            OrderStatus.Delivered
        }
        else -> OrderStatus.Returned
    }
}