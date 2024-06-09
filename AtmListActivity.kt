package com.app.atm.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.atm.R
import com.app.atm.adapter.ATMListAdapter
import com.app.atm.adapter.AtmWiseTransactionAdapter
import com.app.atm.databinding.ActivityAtmListBinding
import com.app.atm.databinding.ActivityAtmhistoryBinding
import com.app.atm.models.AdminAtmHistory
import com.app.atm.models.AtmModel
import com.app.atm.models.TransactionHistory
import com.app.atm.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database

class AtmListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAtmListBinding
    lateinit var adapter: ATMListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtmListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.textTitle.text = "ATM"
        binding.toolbarLayout.backLayout.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        loadAtm()
        setRecyclerView()

    }

    private fun setRecyclerView() {
        adapter = ATMListAdapter(this@AtmListActivity)
        binding.rvRecyclerView.adapter = adapter
        binding.rvRecyclerView.layoutManager = LinearLayoutManager(this@AtmListActivity,
            LinearLayoutManager.VERTICAL,false)
    }

    private fun loadAtm() {
        binding.progressCircular.isVisible = true
        val database = com.google.firebase.ktx.Firebase.database
        val myRef = database.getReference(Constants.atm)
        val atmList = ArrayList<AtmModel>()

        try{
            myRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val atmData = snapshot.value as HashMap<*, *>
                        val values = atmData.values
                        values.forEach {
                            val data = it as HashMap<*, *>
                            val atmDataModel =  AtmModel(
                                atm_location = data["atm_location"].toString(),
                                atm_name  = data["atm_name"].toString(),
                                totalAmount = data["total_amount"].toString(),
                                qrData = data["qrData"].toString(),
                                qrImage = data["qrImage"].toString()

                            )
                            atmList.add(atmDataModel)
                        }
                        adapter.addData(atmList)
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
                   /** handle error **/
                }

            })

        }catch (e:Exception){
            e.printStackTrace()

        }
    }
}