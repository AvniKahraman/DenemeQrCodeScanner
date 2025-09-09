package com.avnikahraman.denemeqrcodescanner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : AppCompatActivity() {

    private lateinit var scanQRBtn: Button
    private lateinit var scannedValueTV: TextView
    private lateinit var scanner: GmsBarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        initVars()
        registerUiListener()

        val button = findViewById<Button>(R.id.Medication)
        button.setOnClickListener {
            val intent = Intent(this, MedicationListActivity::class.java)
            startActivity(intent)
        }
        supportActionBar?.show()


    }

    private fun initVars() {
        scanQRBtn = findViewById(R.id.scanQR)
        scannedValueTV = findViewById(R.id.scannedValue)

        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()

        scanner = GmsBarcodeScanning.getClient(this, options)
    }

    private fun registerUiListener() {
        scanQRBtn.setOnClickListener {
            startScanning()
        }
    }

    private fun startScanning() {
        scanner.startScan()
            .addOnSuccessListener {
                val scannedName = it.rawValue
                scannedValueTV.text = "Scanned Value: $scannedName"
                if (!scannedName.isNullOrEmpty()) {
                    findMedicationByNameAndOpenDetail(scannedName)
                }
            }
            .addOnCanceledListener {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
    private fun findMedicationByNameAndOpenDetail(medicationName: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("medications")
            .whereEqualTo("name", medicationName.lowercase())
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents[0]
                    val medicationId = doc.id
                    val intent = Intent(this, MedicationDetailActivity::class.java)
                    intent.putExtra("medicationId", medicationId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "İlaç bulunamadı: $medicationName", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Firestore hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
