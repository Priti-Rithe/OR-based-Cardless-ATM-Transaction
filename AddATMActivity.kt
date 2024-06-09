package com.app.atm.admin


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.app.atm.BuildConfig
import com.app.atm.R
import com.app.atm.databinding.ActivityAddAtmactivityBinding
import com.app.atm.utils.Constants
import com.app.atm.utils.displayToast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.EnumMap
import java.util.Objects
import java.util.UUID
import kotlin.random.Random


class AddATMActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddAtmactivityBinding
    var data = ""
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAtmactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.backLayout.setOnClickListener(this)
        binding.generateQrCode.setOnClickListener(this)
        binding.submitButton.setOnClickListener(this)

        binding.toolbarLayout.textTitle.text = getString(R.string.add_atm)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.toolbarLayout.backLayout.id -> {
                onBackPressedDispatcher.onBackPressed()
            }
            binding.generateQrCode.id -> {

                binding.qrCodeImage.isVisible = false
                if(binding.inputAtmName.text.toString().isEmpty()){
                    displayToast(getString(R.string.please_enter_atm_name))
                }else if(binding.inputAtmLocation.text.toString().isEmpty()){
                    displayToast(getString(R.string.please_enter_atm_location))
                }else{
                    generateQRCode()
                }


            }
            binding.submitButton.id ->{
                uploadImageToFirebaseStorage()

            }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        binding.progressCircular.isVisible = true
        if(imageUri !=null){
            val imageRef = storageRef.child("qrCode/${UUID.randomUUID()}.jpg")

            imageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        storeDataToFirebase(downloadUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("error -->",e.message.toString())
                    e.printStackTrace()
                }
        }

    }

    private fun storeDataToFirebase(qrImage: String){
        val database = com.google.firebase.ktx.Firebase.database
        val myRef = database.getReference(Constants.atm)

        val atmData = hashMapOf(
            "atm_name" to binding.inputAtmName.text.toString(),
            "atm_location" to binding.inputAtmLocation.text.toString(),
            "qrData" to data.toString(),
            "qrImage" to qrImage,
            "total_amount" to "0"
        )

        try{
            myRef.orderByChild("qrData").equalTo(data)
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            binding.progressCircular.isVisible = false
                            displayToast(getString(R.string.atm_already_exists))

                        }else{
                            myRef.push()
                                .setValue(atmData)
                                .addOnCompleteListener {task ->
                                    if(task.isSuccessful){
                                        binding.progressCircular.isVisible = false
                                        displayToast(getString(R.string.atm_added_successfully))
                                        finish()
                                    }else{
                                        binding.progressCircular.isVisible = false
                                        displayToast(getString(R.string.please_try_again_later))
                                    }
                                }
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

    private fun generateQRCode() {
        binding.progressCircular.isVisible = true
        data = generateRandomAlphaNumeric(10)

        val width = 500
        val height = 500

        val hints: MutableMap<EncodeHintType, Any> = EnumMap(
            EncodeHintType::class.java
        )
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"



        try {
            val writer =  QRCodeWriter()
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints)

            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height
            val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.RGB_565)

            for (x in 0 until matrixWidth) {
                for (y in 0 until matrixHeight) {
                    bitmap.setPixel(
                        x, y,
                        if (bitMatrix[x, y])ContextCompat.getColor(this@AddATMActivity,R.color.black)
                        else ContextCompat.getColor(this@AddATMActivity,R.color.white)
                    )
                }
            }
            binding.qrCodeImage.setImageBitmap(bitmap)

            binding.progressCircular.isVisible = false
            binding.qrCodeImage.isVisible = true
            imageUri = bitmapToUri(this@AddATMActivity,bitmap)
        }catch (e:Exception){
            e.printStackTrace()

        }
    }

    private fun bitmapToUri(context:Context, bitmap: Bitmap): Uri? {
        val file = File(cacheDir, "temp_qr.png")
        file.createNewFile()

        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

        return FileProvider.getUriForFile(
            Objects.requireNonNull(getApplicationContext()),
            BuildConfig.APPLICATION_ID + ".provider", file);

//        return FileProvider.getUriForFile(
//            this,
//            applicationContext.packageName + ".provider",
//            file
//        )

    }

    private fun generateRandomAlphaNumeric(length: Int): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') // Define the character pool
        val random = Random.Default
        return (1..length)
            .map { random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}