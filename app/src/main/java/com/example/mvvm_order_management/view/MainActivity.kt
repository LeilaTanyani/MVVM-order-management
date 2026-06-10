package com.example.mvvm_order_management.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvm_order_management.R
import com.example.mvvm_order_management.databinding.ActivityMainBinding
import com.example.mvvm_order_management.model.LoadingState
import com.example.mvvm_order_management.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter = OrderAdapter(arrayListOf())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()

        initializeUI()
        initializeObservers()

        viewModel.onViewReady()
    }

    private fun initializeUI(){
        binding.ordersRV.adapter = adapter
        binding.ordersRV.layoutManager = LinearLayoutManager(this)

        binding.searchET.doOnTextChanged { text, start, before, count ->
            viewModel.onSearchQuery(text.toString())
        }
    }

    private fun initializeObservers() {
        viewModel.loadingStateLiveData.observe(this, {
            onLoadingStateChanged(it)
        })

        viewModel.ordersLiveData.observe(this, {
            adapter.updateOrders(it)
        })

    }

    private fun onLoadingStateChanged(state: LoadingState) {
        binding.searchET.visibility = if (state == LoadingState.LOADED) View.VISIBLE else View.GONE
        binding.ordersRV.visibility = if (state == LoadingState.LOADED) View.VISIBLE else View.GONE
        binding.errorTV.visibility = if (state == LoadingState.ERROR) View.VISIBLE else View.GONE
        binding.loadingPB.visibility = if (state == LoadingState.LOADING) View.VISIBLE else View.GONE
    }
}


