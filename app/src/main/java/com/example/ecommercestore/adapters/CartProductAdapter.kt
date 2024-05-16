package com.example.ecommercestore.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommercestore.databinding.CartProductItemBinding
import com.example.ecommercestore.databinding.SpecialRvItemBinding
import com.example.ecommercestore.helper.getProductPrice
import com.example.ecommercestore.models.CartProduct
import com.example.ecommercestore.models.Product

class CartProductAdapter:RecyclerView.Adapter<CartProductAdapter.CartProductHolder>() {

    inner class CartProductHolder(val binding: CartProductItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(cartProduct: CartProduct){
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()
                cartProduct.product.offerPercentage?.let {
                    val priceAfterPercentage = it.getProductPrice(cartProduct.product.price)
                    tvProductCartPrice.text = "$ ${String.format("%.2f",priceAfterPercentage)}"
                }
                imageCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?: Color.TRANSPARENT))
                tvCartProductSize.text = cartProduct.selectedSize?:"".also {
                    imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

    // refresh item that updated only not all list of items
    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            // compare update item only
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            // compare whole object
            return oldItem == newItem
        }
    }
    // responsible for update list and get the list
    val diff = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductHolder {
        return CartProductHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductHolder, position: Int) {
        // bind items
        val cartProduct = diff.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener{
            onProductClick?.invoke(cartProduct)
        }

        holder.binding.imagePlus.setOnClickListener{
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.imageMinus.setOnClickListener{
            onMinusClick?.invoke(cartProduct)
        }

    }
    var onProductClick:((cartProduct: CartProduct)-> Unit)?=null
    var onPlusClick:((cartProduct: CartProduct)-> Unit)?=null
    var onMinusClick:((cartProduct: CartProduct)-> Unit)?=null


}