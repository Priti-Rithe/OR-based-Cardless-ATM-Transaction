package com.app.atm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.app.atm.R
import com.app.atm.databinding.ItemDepositAmountBinding
import com.app.atm.models.TransactionHistory

class TransactionHistoryAdapter(val list:ArrayList<TransactionHistory>,val mContext:Context,val isUser:Boolean = false):RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {
    class ViewHolder(val binding:ItemDepositAmountBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDepositAmountBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val currentItem = list[position]
        holder.binding.apply {
                tvUserName.text = if(isUser) currentItem.atmModel.atm_name else currentItem.userModel.name
                ivType.setImageDrawable(
                    if(currentItem.transactionType == "D")
                        AppCompatResources.getDrawable(mContext, R.drawable.ic_up_right_arrow)
                    else
                        AppCompatResources.getDrawable(mContext, R.drawable.ic_left_down_arrow)
                )
            tvTransactionType.text = if(currentItem.transactionType == "D") "Deposit" else "Withdraw"
            tvTransactionAmount.text = if(currentItem.transactionType == "D") "+ \u20B9 ${currentItem.transactionAmount}" else "- \u20B9 ${currentItem.transactionAmount}"
        }
    }
}