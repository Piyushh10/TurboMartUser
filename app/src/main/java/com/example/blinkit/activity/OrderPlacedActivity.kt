package com.example.blinkit.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.blinkit.utils.CartListener
import com.example.blinkit.utils.Constants
import com.example.blinkit.R
import com.example.blinkit.utils.Utils
import com.example.blinkit.adapters.AdapterCartProducts
import com.example.blinkit.databinding.ActivityOrderPlacedBinding
import com.example.blinkit.databinding.AddressLayoutBinding
import com.example.blinkit.models.Orders
import com.example.blinkit.viewmodels.UserViewmodel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.launch
import org.json.JSONObject

class OrderPlacedActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityOrderPlacedBinding
    val viewModel: UserViewmodel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private var cartListener: CartListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlacedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllProducts()
        setStatusBarColor()
        backToMainActivity()
        onPlaceOrderClicked()
    }

    private fun onPlaceOrderClicked() {
        binding.btnNext.setOnClickListener {
            viewModel.getAddressStatus().observe(this) { status ->
                if (status) {
                    startRazorpayPayment()
                } else {
                    val addressLayoutBinding =
                        AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val alertDialog = AlertDialog.Builder(this).setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAddAddress.setOnClickListener {
                        saveAddress(alertDialog, addressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun startRazorpayPayment() {
        // Calculate total price from cart
        var totalPrice = 0
        viewModel.getAll().observe(this) { cartProductsList ->
            for (products in cartProductsList) {
                val price = products.productPrice?.replace("₹", "")?.toIntOrNull() ?: 0
                val itemCount = products.productCount ?: 0
                totalPrice += price * itemCount
            }
            if (totalPrice < 1) {
                Toast.makeText(this, "Cart is empty or invalid amount", Toast.LENGTH_LONG).show()
                return@observe
            }
            // Add delivery charge if total is below 200
            if (totalPrice < 200) {
                totalPrice += 40
            }
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_rg8RnYvnkJmjB5") // TODO: Replace with your Razorpay key
            try {
                val options = JSONObject()
                options.put("name", "TurboMart Order")
                options.put("description", "Order Payment")
                options.put("currency", "INR")
                options.put("amount", (totalPrice * 100).toString()) // Amount in paise
                options.put("prefill.email", "user@email.com") // Optional
                options.put("prefill.contact", "9999999999") // Optional
                checkout.open(this, options)
            } catch (e: Exception) {
                Toast.makeText(this, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        Toast.makeText(this, "Payment Successful: $razorpayPaymentID", Toast.LENGTH_LONG).show()
        // Place order as before
        saveOrder()
        lifecycleScope.launch {
            viewModel.deleteCartProducts()
        }
        viewModel.savingCartItemCount(0)
        cartListener?.hideCartLayout()
        startActivity(Intent(this, UsersMainActivity::class.java))
        finish()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment failed: $response", Toast.LENGTH_LONG).show()
    }

    private fun saveOrder() {
        viewModel.getAll().observe(this) { cartProductList ->

            if (cartProductList.isNotEmpty()) {
                viewModel.getUserAddress { address ->
                    val orders = Orders(
                        orderId = Utils.getRandomId(),
                        orderList = cartProductList,
                        userAddress = address,
                        orderStatus = 0,
                        orderDate = Utils.getCurrentDate(),
                        orderingUserId = Utils.getCurrentUserId()
                    )
                    viewModel.saveOrderProducts(orders)
                    //notification
                    lifecycleScope.launch {
                        Log.d("GGG", "uid:"+cartProductList[0].adminUid.toString())
                        viewModel.sendNotification(
                            cartProductList[0].adminUid!!,
                            "Ordered",
                            "Some products has been ordered"
                        )
                    }


                }
                for (products in cartProductList) {
                    val count = products.productCount
                    val stock = products.productStock?.minus(count!!)
                    viewModel.saveProductsAfterOrder(stock!!, products)

                }
            }

        }
    }

    private fun saveAddress(alertDialog: AlertDialog?, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialog(this, "Processing....")
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNo.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etAddress.text.toString()

        val address = "$userPinCode, $userDistrict($userState), $userAddress, $userPhoneNumber"


        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()
        }

        alertDialog?.dismiss()
        Utils.hideDialog()
    }

    private fun backToMainActivity() {
        binding.tbOrderFragment.setOnClickListener {
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }

    private fun getAllProducts() {
        viewModel.getAll().observe(this) { cartProductsList ->
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItem.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductsList)

            var totalPrice = 0

            for (products in cartProductsList) {
                val price = products.productPrice?.substring(1)?.toInt()
                val itemCount = products.productCount!!
                totalPrice += price!! * itemCount
            }

            binding.tvSubTotal.text = "₹" + totalPrice.toString()

            if (totalPrice < 200) {
                binding.tvDeliveryCharge.text = "₹40"
                totalPrice += 40;
            }

            binding.tvFinalTotal.text = "₹" + totalPrice.toString()

        }
    }

    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors =
                ContextCompat.getColor(this@OrderPlacedActivity, R.color.diyaFlame)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}