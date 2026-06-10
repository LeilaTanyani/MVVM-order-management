package com.example.mvvm_order_management.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_order_management.databinding.OrderItemBinding
import com.example.mvvm_order_management.model.Order

class OrderAdapter(private val orders: ArrayList<Order>): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun updateOrders(newOrders: List<Order>){
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    inner class OrderViewHolder(val binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root){

        private val cardColors = listOf(
            "#FFE0E0", // Soft Red
            "#E0F7FA", // Soft Cyan
            "#E8F5E9", // Soft Green
            "#FFF9C4", // Soft Yellow
            "#F3E5F5", // Soft Purple
            "#FFF3E0", // Soft Orange
            "#E1BEE7", // Soft Light Purple
            "#D1C4E9"  // Soft Deep Purple
        )
        fun bind(order: Order){
            binding.orderId.text = order.id.toString()
            binding.product.text = order.product.toString()
            binding.user.text = order.user.toString()

            val colorString = cardColors[bindingAdapterPosition % cardColors.size]
            binding.root.setCardBackgroundColor(Color.parseColor(colorString))
        }
    }

}