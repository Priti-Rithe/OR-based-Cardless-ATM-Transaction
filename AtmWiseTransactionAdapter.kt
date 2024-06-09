package com.app.atm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.atm.databinding.ItemAtmHistoryBinding
import com.app.atm.models.AdminAtmHistory
import com.app.atm.models.TransactionHistory
import com.bumptech.glide.Glide

class AtmWiseTransactionAdapter(val mContext: Context):RecyclerView.Adapter<AtmWiseTransactionAdapter.ViewHolder>() {
    class ViewHolder(val binding:ItemAtmHistoryBinding):RecyclerView.ViewHolder(binding.root)
    val list = ArrayList<AdminAtmHistory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAtmHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.binding.apply {
            tvAtmName.text = currentItem.atmName
            rvTransaction.adapter = TransactionHistoryAdapter(currentItem.data as ArrayList<TransactionHistory>,mContext)
            rvTransaction.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false)
            if(currentItem.isShowData){
                rvTransaction.isVisible = true
                ivArrow.rotation = 180f
            }else{
                rvTransaction.isVisible = false
                ivArrow.rotation = 90f
            }
            ivArrow.setOnClickListener {
                currentItem.isShowData = !currentItem.isShowData
                notifyItemChanged(position)
            }
        }
    }

    fun addData(dataList:ArrayList<AdminAtmHistory>){
        list.clear()
        list.addAll(dataList)

        notifyDataSetChanged()
    }
}