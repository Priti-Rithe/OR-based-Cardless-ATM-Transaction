package com.app.atm.admin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.atm.R
import com.app.atm.UserSelectionActivity
import com.app.atm.databinding.ActivityAdminDashboardBinding
import com.app.atm.utils.Constants

class AdminDashboardActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(Constants.preference, Context.MODE_PRIVATE)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.cardAddATM.setOnClickListener(this)
        binding.cardATMHistory.setOnClickListener(this)
        binding.cardLogout.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.cardAddATM.id -> {
                Intent(applicationContext, AddATMActivity::class.java).apply {
                    startActivity(this)
                }
            }

            binding.cardATMHistory.id -> {
                Intent(applicationContext, ATMHistoryActivity::class.java).apply {
                    startActivity(this)
                }
            }

            binding.cardLogout.id -> {
                showLogoutPopup()

            }
        }
    }

    private fun showLogoutPopup() {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)


        alertDialogBuilder.setTitle(getString(R.string.logout))
        alertDialogBuilder.setMessage(getString(R.string.do_you_really_want_to_logout))


        alertDialogBuilder.setPositiveButton(getString(R.string.logout)) { dialog, which ->
            sharedPreferences.edit().clear().apply()
            Intent(applicationContext, UserSelectionActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(this)
                finish()
            }
            dialog.dismiss()
            // Close the dialog
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss() // Close the dialog
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }
}