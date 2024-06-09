package com.app.atm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.atm.databinding.ItemAtmListBinding
import com.app.atm.models.AtmModel
import com.bumptech.glide.Glide

class ATMListAdapter(val mContext:Context):RecyclerView.Adapter<ATMListAdapter.ViewHolder>() {
    class ViewHolder(val binding:ItemAtmListBinding):RecyclerView.ViewHolder(binding.root)

    val list = ArrayList<AtmModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAtmListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.binding.apply {
          tvAtmName.text = list[position].atm_name
          Glide.with(mContext)
              .load(list[position].qrImage) // Optional: You can apply transformations here
              .into(ivQR)
      }
    }

    fun addData(dataList:ArrayList<AtmModel>){
        list.clear()
        list.addAll(dataList)

        notifyDataSetChanged()
    }
}