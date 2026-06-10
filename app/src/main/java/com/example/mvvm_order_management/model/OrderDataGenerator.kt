package com.example.mvvm_order_management.model

import android.util.Log
import kotlinx.coroutines.delay
import kotlin.random.Random

// The data generator acts as a simulator of our Database
// A singleton
object OrderDataGenerator {
    private val currentUsers = arrayListOf<User>()
    private val currentProducts = arrayListOf<Product>()
    private val currentOrders = arrayListOf<Order>()

    private const val stdDelay = 2000L

    init {
        for (i in 1..100){
            generateUsers()
            generateProducts()
        }
        for (i in 1..100){
            generateOrders()
        }
    }

    suspend fun getAllOrders(): List<Order> {
        delay(stdDelay)
        return currentOrders
    }

    suspend fun searchOrders (query: String): List<Order> {
        delay(stdDelay)
        return currentOrders.filter {
            it.product.name.lowercase().contains(query.lowercase()) or
                    it.product.brand.lowercase().contains(query.lowercase()) or
                    it.user.name.lowercase().contains(query.lowercase()) or
                    it.user.email.lowercase().contains(query.lowercase())
        }
    }
    private fun generateUsers(){
        val index = Random.nextInt(usersList.size)
        val userName = usersList[index]
        val email = "$userName@gmail.com"
        val userId = 1000 + currentUsers.size

        currentUsers.add(User(userId, userName, email))
    }

    private fun generateProducts(){
        val brandIndex = Random.nextInt(brandsList.size)
        val brandName = brandsList[brandIndex]
        val productIndex = Random.nextInt(productsList.size)
        val productName = productsList[productIndex]
        val price = Random.nextInt(1000)
        val productId = 20000 + currentProducts.size

        currentProducts.add(Product(productId, productName, brandName, price))
    }

    private fun generateOrders(){
        val user = currentUsers[Random.nextInt(currentUsers.size)]
        val product = currentProducts[Random.nextInt(currentProducts.size)]
        val orderId = 30000 + currentOrders.size

        currentOrders.add(Order(orderId, user, product))
        Log.d("OrderGenerator", "$orderId $user $product")
    }
}