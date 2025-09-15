package com.avnikahraman.denemeqrcodescanner

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)

        // Eğer daha önce remember me seçildiyse otomatik login
        val rememberMe = sharedPref.getBoolean("rememberMe", false)
        val savedUser = auth.currentUser
        if (rememberMe && savedUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val cbRememberMe = findViewById<CheckBox>(R.id.cbRememberMe)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoRegister = findViewById<Button>(R.id.btnGoRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val remember = cbRememberMe.isChecked
            loginUser(email, password, remember)
        }

        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
    }

    private fun loginUser(email: String, password: String, remember: Boolean) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Login başarılı", Toast.LENGTH_SHORT).show()
                sharedPref.edit().putBoolean("rememberMe", remember).apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login hatası: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
