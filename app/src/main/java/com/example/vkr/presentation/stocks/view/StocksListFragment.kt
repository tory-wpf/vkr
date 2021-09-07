package com.example.vkr.presentation.stocks.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vkr.databinding.FragmentStocksBinding
import com.example.vkr.domain.entity.Stock
import com.example.vkr.domain.utils.hide
import com.example.vkr.domain.utils.show
import com.example.vkr.presentation.stocks.IMainActivity
import com.example.vkr.presentation.stocks.adapters.StocksRecyclerAdapter
import com.example.vkr.presentation.stocks.adapters.StocksViewPagerAdapter
import com.example.vkr.presentation.stocks.presenter.IStocksPresenter
import com.example.vkr.presentation.stocks.presenter.StocksPresenter

class StocksListFragment : Fragment(), StocksRecyclerAdapter.StockClickListener, IStocksFragment {
    private lateinit var binding: FragmentStocksBinding
    private val presenter: IStocksPresenter = StocksPresenter()
    private lateinit var adapter: StocksRecyclerAdapter
    private var isNoRequest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isNoRequest = it.getBoolean("No_request", false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStocksBinding.inflate(inflater, container, false)
        adapter = StocksRecyclerAdapter(mutableListOf(), this)
        binding.stocksList.adapter = adapter
        (presenter as StocksPresenter).attachView(this)

        if (!isNoRequest)
            presenter.bindStockList("^NDX")
        else
            updateStocks()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (isNoRequest)
            updateStocks()
        else
            adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (presenter as StocksPresenter).detachView()
    }

    override fun updateStocks() {
        presenter.bindFavourites()
    }

    override fun getRVAdapter(): StocksRecyclerAdapter = adapter

    override fun onFavouriteIconClick(
        stock: Stock?,
        holder: StocksRecyclerAdapter.StocksViewHolder
    ) {
        (presenter as StocksPresenter).onFavouriteIconClick(stock, holder)
    }

    override fun onSnippetClick() {
        Toast.makeText(requireContext(), "Stock!!!", Toast.LENGTH_LONG).show()
    }

    override fun showReload() {
        binding.reloadLayout.root.show()

        binding.reloadLayout.reloadBtn.setOnClickListener {
            binding.reloadLayout.root.hide()
            presenter.bindStockList("^NDX")
        }
    }

    override fun bindStocks(data: MutableList<Stock>?) {
        data?.let { adapter.addItems(it) }
    }

    override fun bindStock(item: Stock?) {
        item?.let { adapter.addItem(it) }
    }

    override fun getVPAdapter(): StocksViewPagerAdapter {
        val activity = requireActivity() as IMainActivity
        val vp = activity.getVP()
        return vp.adapter as StocksViewPagerAdapter
    }
}