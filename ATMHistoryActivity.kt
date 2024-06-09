package com.app.atm.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.atm.R
import com.app.atm.adapter.AtmWiseTransactionAdapter
import com.app.atm.databinding.ActivityAtmhistoryBinding
import com.app.atm.models.AdminAtmHistory
import com.app.atm.models.AtmModel
import com.app.atm.models.TransactionHistory
import com.app.atm.models.UserModel
import com.app.atm.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database


class ATMHistoryActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityAtmhistoryBinding
    lateinit var adapter:AtmWiseTransactionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtmhistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.backLayout.setOnClickListener(this)
        binding.toolbarLayout.textTitle.text = getString(R.string.atm_history)
        binding.toolbarLayout.logoImageHeader.isVisible = true
        binding.toolbarLayout.logoImageHeader.setOnClickListener {
            startActivity(Intent(this@ATMHistoryActivity,AtmListActivity::class.java))
        }
        loadAtmHistory()
        setRecyclerView()

    }

    private fun setRecyclerView() {
        adapter = AtmWiseTransactionAdapter(this@ATMHistoryActivity)
        binding.rvRecyclerView.adapter = adapter
        binding.rvRecyclerView.layoutManager = LinearLayoutManager(this@ATMHistoryActivity,LinearLayoutManager.VERTICAL,false)


    }

    private fun loadAtmHistory() {
        binding.progressCircular.isVisible = true
        val database = com.google.firebase.ktx.Firebase.database
        val myRef = database.getReference(Constants.transaction)
        val dataMap: HashMap<String, ArrayList<TransactionHistory>> =
            HashMap()
        val atmHistoryModel = ArrayList<AdminAtmHistory>()

        try{
            myRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val donationData = snapshot.value as HashMap<*, *>
                        val values = donationData.values
                        values.forEach {
                            val data = it as HashMap<*, *>
                            Log.e("transaction --",data.toString())
                            val atmData = data["atmData"] as HashMap<*,*>
                            val userData = data["userData"] as HashMap<*,*>
                            val key = "${atmData["atm_name"]}"
                            val transactionData = TransactionHistory(
                               atmModel =  AtmModel(
                                    atm_location = atmData["atm_location"].toString(),
                                    atm_name  = atmData["atm_name"].toString(),
                                    totalAmount = atmData["total_amount"].toString(),
                                    qrData = atmData["qrData"].toString(),
                                    qrImage = atmData["qrImage"].toString()

                                ),
                                transactionAmount = data["transaction_amount"].toString(),
                                transactionType = data["transaction_type"].toString(),
                                userModel = UserModel(
                                    name = userData["name"].toString(),
                                    phone = userData["phone"].toString(),
                                    address = userData["address"].toString(),
                                    password = userData["password"].toString(),
                                )
                            )
                            if(dataMap.containsKey(key)){
                                dataMap[key]?.add(transactionData)

                            }else{
                              dataMap[key] = arrayListOf(transactionData)
                            }

                        }
                        atmHistoryModel.clear()
                        dataMap.forEach { s, transactionHistories ->
                            atmHistoryModel.add(
                                AdminAtmHistory(
                                    atmName = s,
                                    atmQR = transactionHistories[0].atmModel.qrImage,
                                    data = transactionHistories
                                )
                            )
                        }
                        Log.e("list-size",atmHistoryModel.size.toString())
                        adapter.addData(atmHistoryModel)
                        binding.progressCircular.isVisible = false
                        binding.tvError.isVisible = false
                        binding.rvRecyclerView.isVisible = true
                    }else{
                        binding.progressCircular.isVisible = false
                        binding.tvError.isVisible = true
                        binding.rvRecyclerView.isVisible = false

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                   /** handle Error **/
                }

            })
        }catch (e:Exception){
            e.printStackTrace()

        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.toolbarLayout.backLayout.id -> {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}