package com.avnikahraman.denemeqrcodescanner

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.avnikahraman.denemeqrcodescanner.R.id.scannedValue
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : AppCompatActivity() {

    private lateinit var scanQRBtn : Button
    private lateinit var scannedValueTV : TextView
    private var isScannerInstalled =false
    private lateinit var Scanner : GmsBarcodeScanner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            initVars()
            installGoogleScanner()
            regiterUiListener()


        }

    private fun installGoogleScanner(){

        val moduleInstall = ModuleInstall.getClient(this)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(this))
            .build()

        moduleInstall.installModules(moduleInstallRequest).addOnSuccessListener {

            isScannerInstalled = true
        }
            .addOnFailureListener {
                isScannerInstalled = false
                Toast.makeText(this , it.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun initVars(){

        scanQRBtn   = findViewById(R.id.scanQR)
        scannedValueTV = findViewById(R.id.scannedValue)

        val options = initializeGoogleScanner()
        Scanner = GmsBarcodeScanning.getClient(this,options)
    }

    private fun initializeGoogleScanner(): GmsBarcodeScannerOptions {
       return GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom().build()
    }
    private fun regiterUiListener(){

        scanQRBtn.setOnClickListener {

            if (isScannerInstalled){
                startScanning()

            }else{
                Toast.makeText(this , "Please try again...", Toast.LENGTH_SHORT).show()

            }


        }

    }

    private fun startScanning(){

        Scanner.startScan().addOnSuccessListener {
            val result = it.rawValue
            result?.let {
                scannedValueTV.text = buildString {
                    append("Scanned Value:  ")
                    append(it)
                }
            }
        }.addOnCanceledListener {

            Toast.makeText(this , "Cancelled", Toast.LENGTH_SHORT).show()


        }.addOnFailureListener {

            Toast.makeText(this , it.message, Toast.LENGTH_SHORT).show()

        }

    }

    }
