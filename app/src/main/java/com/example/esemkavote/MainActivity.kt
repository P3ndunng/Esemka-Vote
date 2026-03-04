package com.example.esemkavote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.esemkavote.api.ApiClient
import com.example.esemkavote.api.model.AuthDTO
import com.example.esemkavote.api.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.txtemail)
        val etPassword = findViewById<EditText>(R.id.txtpassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.instance.login(AuthDTO(email, password))
                .enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (!response.isSuccessful) {
                            val err = response.errorBody()?.string()
                            android.util.Log.e("DEBUG_LOGIN", "Login gagal code=${response.code()} err=$err")
                            Toast.makeText(this@MainActivity, "Login gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                            return
                        }

                        val body = response.body()
                        if (body == null) {
                            Toast.makeText(this@MainActivity, "Login gagal: body null", Toast.LENGTH_SHORT).show()
                            return
                        }

                        val empId = body.id
                        val emailLower = (body.email ?: email).lowercase()

                        getSharedPreferences("EsemkaPrefs", MODE_PRIVATE).edit()
                            .putInt("EMP_ID", empId)
                            .putString("EMAIL", emailLower)
                            .putString("NAME", body.name ?: "")
                            .putString("DIVISION", body.division ?: "")
                            .putString("TOKEN", body.token ?: "")
                            .apply()

                        android.util.Log.d("DEBUG_LOGIN", "Login OK empId=$empId email=$emailLower")

                        Toast.makeText(this@MainActivity, "Login berhasil! empID=$empId", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        finish()
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        android.util.Log.e("DEBUG_LOGIN", "Koneksi gagal: ${t.message}", t)
                        Toast.makeText(this@MainActivity, "Error koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}