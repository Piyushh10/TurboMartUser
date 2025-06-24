package com.example.blinkit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.blinkit.databinding.ItemViewBestsellerBinding
import com.example.blinkit.models.BestSeller

class BestSellerAdapter(val seeAllBtnClicked: (BestSeller) -> Unit) : RecyclerView.Adapter<BestSellerAdapter.BestSellerAdapterViewHolder>() {
    class BestSellerAdapterViewHolder(val binding: ItemViewBestsellerBinding) :
        ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<BestSeller>() {
        override fun areItemsTheSame(oldItem: BestSeller, newItem: BestSeller): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BestSeller, newItem: BestSeller): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestSellerAdapterViewHolder {
        return BestSellerAdapterViewHolder(
            ItemViewBestsellerBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestSellerAdapterViewHolder, position: Int) {
        val productType = differ.currentList[position]
        holder.binding.apply {
            tvCategory.text = productType.productType ?: ""
            tvTotalProducts.text = productType.products?.size.toString() + " products"

            val listOfIv = listOf(ivProduct1, ivProduct2, ivProduct3)
            val totalProducts = productType.products?.size ?: 0

            // Hide all image views and badge by default
            listOfIv.forEach { it.visibility = View.GONE }
            tvProductCount.visibility = View.GONE

            // Show images as per product count
            for (i in 0 until minOf(3, totalProducts)) {
                listOfIv[i].visibility = View.VISIBLE
                Glide.with(holder.itemView).load(productType.products!![i].productImagesUris?.getOrNull(0)).into(listOfIv[i])
            }
            // Show +N badge only if more than 3 products
            if (totalProducts > 3) {
                tvProductCount.visibility = View.VISIBLE
                tvProductCount.text = "+${totalProducts - 3}"
            }
        }

        holder.itemView.setOnClickListener {
            seeAllBtnClicked(productType)
        }
    }
}