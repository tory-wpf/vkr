package com.example.vkr.presentation.stocks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vkr.App
import com.example.vkr.R
import com.example.vkr.databinding.ItemStockSnippetBinding
import com.example.vkr.domain.entity.Stock
import com.example.vkr.presentation.stocks.presenter.StocksPresenter

class StocksRecyclerAdapter(
    private var list: MutableList<Stock>?,
    private val listener: StockClickListener
) : RecyclerView.Adapter<StocksRecyclerAdapter.StocksViewHolder>() {


    inner class StocksViewHolder(private val binding: ItemStockSnippetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = list?.get(position)

            binding.run {
                companySymbol.text = item?.ticker
                companyName.text = item?.name
                if (item?.currentPrice != null)
                    companyCurrentPrice.text = item?.currentPrice?.toString() + "$"
                else
                    companyCurrentPrice.text = ""
                companyDayDelta.text = item?.cutDelta()
                if (item?.cutDelta()?.first().toString() == "+")
                    companyDayDelta.setTextColor(ContextCompat.getColor(App.ctx, R.color.green))
                else if (item?.cutDelta()?.first().toString() == "-")
                    companyDayDelta.setTextColor(ContextCompat.getColor(App.ctx, R.color.red))
                Glide.with(App.ctx).load(item?.logo).into(companyLogo)
                if (item?.isFavourite == true)
                    binding.favouriteIcon.setImageResource(R.drawable.ic_favourite)
                else
                    binding.favouriteIcon.setImageResource(R.drawable.ic_no_favourite)
            }

            itemView.setOnClickListener {
                listener.onSnippetClick()
            }

            binding.favouriteIcon.setOnClickListener {
                listener.onFavouriteIconClick(item, this)
            }

            itemView.setOnLongClickListener{
                listener.onFavouriteIconClick(item, this)
                true
            }
        }

        fun changeIcon(@DrawableRes icon: Int) {
            binding.favouriteIcon.setImageResource(icon)
        }
    }

    fun updateItem(position: Int, stockList: MutableList<Stock>){
        list?.set(position, stockList[position])
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StocksRecyclerAdapter.StocksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStockSnippetBinding.inflate(inflater, parent, false)
        return StocksViewHolder(binding)
    }

    fun clearData() {
        this.list = mutableListOf()
        notifyDataSetChanged()
    }

    fun addItems(list: MutableList<Stock>) {
        clearData()
        this.list = list
        notifyDataSetChanged()
    }

    fun addItem(item: Stock?) {
        item?.let {
            list?.add(it)
            notifyItemInserted((list?.size ?: 0) - 1)
        }
    }

    override fun onBindViewHolder(holder: StocksRecyclerAdapter.StocksViewHolder, position: Int) =
        holder.bind(position)

    override fun getItemCount(): Int = list?.size ?: 0

    interface StockClickListener {

        fun onFavouriteIconClick(stock: Stock?, holder: StocksViewHolder)

        fun onSnippetClick()
    }
}