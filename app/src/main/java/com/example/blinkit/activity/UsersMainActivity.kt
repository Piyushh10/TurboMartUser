package com.example.blinkit.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.blinkit.utils.CartListener
import com.example.blinkit.adapters.AdapterCartProducts
import com.example.blinkit.databinding.ActivityUsersBinding
import com.example.blinkit.databinding.BsCartProductsBinding
import com.example.blinkit.roomdb.CartProducts
import com.example.blinkit.viewmodels.UserViewmodel
import com.google.android.material.bottomsheet.BottomSheetDialog

class UsersMainActivity : AppCompatActivity(), CartListener {

    private lateinit var binding: ActivityUsersBinding
    val viewModel: UserViewmodel by viewModels()
    private lateinit var cartProductsList :List<CartProducts>
    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllProducts()
        onCartClicked()
        onNextButtonClicked()
    }

    private fun onNextButtonClicked() {
       binding.btnNext.setOnClickListener {
           startActivity(Intent(this,OrderPlacedActivity::class.java))
       }
    }

    private fun getAllProducts() {
        viewModel.getAll().observe(this) { cartProductsList ->
            this.cartProductsList = cartProductsList
            val count = cartProductsList.sumOf { it.productCount ?: 0 }
            if (count > 0) {
                binding.llCart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = count.toString()
            } else {
                binding.llCart.visibility = View.GONE
                binding.tvNumberOfProductCount.text = "0"
            }
        }
    }

    private fun onCartClicked() {
        binding.llItemCart.setOnClickListener {
            val bsCartProductBinding =  BsCartProductsBinding.inflate(LayoutInflater.from(this))
            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductBinding.root)

            // Update the bottom sheet's cart count from the actual cart
            val count = cartProductsList.sumOf { it.productCount ?: 0 }
            bsCartProductBinding.tvNumberOfProductCount.text = count.toString()

            bsCartProductBinding.btnNext.setOnClickListener {
                startActivity(Intent(this,OrderPlacedActivity::class.java))
            }

            adapterCartProducts=AdapterCartProducts()
            bsCartProductBinding.rvProductItems.adapter=adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductsList)

            bs.show()
        }
    }

    override fun showCartLayout(itemCount: Int) { /* No-op, handled by observer */ }
    override fun savingCartItemCount(itemCount: Int) { /* No-op, handled by observer */ }
    override fun hideCartLayout() {
        binding.llCart.visibility=View.GONE
        binding.tvNumberOfProductCount.text="0"
    }
}