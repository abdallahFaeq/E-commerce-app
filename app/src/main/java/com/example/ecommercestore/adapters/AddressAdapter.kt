package com.example.ecommercestore.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ecommercestore.R
import com.example.ecommercestore.databinding.AddressRvItemBinding
import com.example.ecommercestore.models.Address

class AddressAdapter :Adapter<AddressAdapter.AddressHolder>(){
    inner class AddressHolder(val binding:AddressRvItemBinding):ViewHolder(binding.root){
        fun bind(address:Address,isSelected:Boolean){
            binding.buttonAddress.text = address.addressTitle
            if (isSelected){
                // selected button case
                binding.buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
            }else{
                // unselected button case
                binding.buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.white))
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    var differ = AsyncListDiffer(this,diffUtil)

    fun deleteItem(position:Int){
        val newList = differ.currentList.toMutableList()
        newList.removeAt(position)
        differ.submitList(newList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        return AddressHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var selectedAddress = -1
    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = differ.currentList[position]

        holder.bind(address, selectedAddress == holder.adapterPosition)

        holder.binding.buttonAddress.setOnClickListener{
            if (selectedAddress<0){
                // unselected case
                notifyItemChanged(selectedAddress)
            }
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)

            onItemClick?.invoke(address)
        }
        holder.binding.buttonAddress.setOnLongClickListener(object :OnLongClickListener{
            override fun onLongClick(view: View?): Boolean {
                onLongClick?.invoke()
                return true
            }

        })

    }

    // to solve this issue will use this
    // when selected address and you want to add address will show two addresses selected
    init {
        differ.addListListener{_,_ ->
            notifyItemChanged(selectedAddress)
        }
    }

    var onItemClick :((Address)->Unit)?=null
    var onLongClick : (()->Unit) ?=null
}