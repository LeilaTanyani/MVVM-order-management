package com.example.mvvm_order_management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.mvvm_order_management.model.LoadingState
import com.example.mvvm_order_management.model.Order
import com.example.mvvm_order_management.model.OrderDataGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    val ordersLiveData = MediatorLiveData<List<Order>>()
    private val _queryLiveData = MutableLiveData<String>()
    private val _allOrdersLiveData = MutableLiveData<List<Order>>()
    private var _searchOrdersLiveData: LiveData<List<Order>>
    val loadingStateLiveData = MutableLiveData<LoadingState>()

    private var searchJob: Job? = null
    private val debouncePeriod = 500L

    init {

        _searchOrdersLiveData = _queryLiveData.switchMap { it ->
            fetchOrderByQuery(it)
        }
        ordersLiveData.addSource(_allOrdersLiveData){
            ordersLiveData.value = it
        }
        ordersLiveData.addSource(_searchOrdersLiveData){
            ordersLiveData.value = it
        }

    }

    fun onViewReady(){
        if(_allOrdersLiveData.value.isNullOrEmpty())
            fetchAllOrders()
    }

    private fun fetchAllOrders(){
        loadingStateLiveData.value = LoadingState.LOADING
        //coroutine starts running here
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Grab your data on the IO Thread
                val orders = OrderDataGenerator.getAllOrders()

                // 2. Switch back to the Main thread to update UI states together safely
                launch(Dispatchers.Main) {
                    _allOrdersLiveData.value = orders
                    loadingStateLiveData.value = LoadingState.LOADED
                }
            }catch (e: Exception){
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }

        }

    }

    fun onSearchQuery(query: String){
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            if(query.isEmpty()){
                fetchAllOrders()
            }else {
               _queryLiveData.postValue(query)
            }
        }

    }

    private fun fetchOrderByQuery(query: String): LiveData<List<Order>> {
        // The liveData{} builder automatically runs on a background thread (Dispatchers.IO by default or context-aware)
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            loadingStateLiveData.postValue(LoadingState.LOADING)
            try {
                val orders = OrderDataGenerator.searchOrders(query)
                // equivalent to postValue
                emit(orders)
                loadingStateLiveData.postValue(LoadingState.LOADED)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }
}