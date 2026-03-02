package com.example.esemkavote

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.esemkavote.api.ApiClient
import com.example.esemkavote.api.model.AuthDTO
import com.example.esemkavote.api.model.LoginResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail    = findViewById<EditText>(R.id.txtemail)
        val etPassword = findViewById<EditText>(R.id.txtpassword)
        val btnLogin   = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.instance.login(AuthDTO(email, password))
                .enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val accessToken = response.body()?.access_token ?: ""
                            val bearerToken = "Bearer $accessToken"

                            // Decode JWT untuk ambil empID
                            val empId = decodeEmpIdFromJwt(accessToken)
                            android.util.Log.d("DEBUG_JWT", "EmpID hasil decode: $empId")

                            getSharedPreferences("EsemkaPrefs", MODE_PRIVATE).edit()
                                .putString("TOKEN", bearerToken)
                                .putString("EMP_ID", empId)
                                .apply()

                            Toast.makeText(this@MainActivity, "Login Berhasil! EmpID=$empId", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()

                        } else {
                            Toast.makeText(this@MainActivity, "Login Gagal: Periksa Email/Password", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Error Koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun decodeEmpIdFromJwt(token: String): String {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return "1"

            // Fix Base64 padding
            var base64 = parts[1].replace('-', '+').replace('_', '/')
            while (base64.length % 4 != 0) base64 += "="

            val decoded = Base64.decode(base64, Base64.DEFAULT)
            val payload = String(decoded, Charsets.UTF_8)

            android.util.Log.d("DEBUG_JWT", "JWT Payload: $payload")

            val json = JSONObject(payload)

            // Print semua keys untuk debugging
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                android.util.Log.d("DEBUG_JWT", "  claim '$k' = '${json.opt(k)}'")
            }

            // Coba semua kemungkinan nama field empID
            val candidates = listOf(
                "empID", "emp_id", "EmpID", "EmployeeId", "employee_id",
                "nameid", "sub", "id", "userId", "user_id",
                "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier"
            )

            for (key in candidates) {
                val value = json.optString(key)
                if (value.isNotEmpty() && value != "null") {
                    android.util.Log.d("DEBUG_JWT", "Pakai key='$key' value='$value'")
                    return value
                }
            }

            "1" // fallback
        } catch (e: Exception) {
            android.util.Log.e("DEBUG_JWT", "Decode error: ${e.message}")
            "1"
        }
    }
}