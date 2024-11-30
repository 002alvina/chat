package com.example.chat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.chat.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val login = findViewById<TextView>(R.id.editTextEmail)
        val password = findViewById<TextView>(R.id.editTextPassword)
        val button = findViewById<Button>(R.id.buttonLogin)
        val text = findViewById<TextView>(R.id.textViewRegister)

        button.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = NetworkService.logIn(login.text.toString(), password.text.toString())
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val userId = loginResponse?.userId
                        val accessToken = loginResponse?.token?.accessToken
                        val refreshToken = loginResponse?.token?.refreshToken
                        text.text =
                            "User ID: $userId\nAccess Token: $accessToken\nRefresh Token: $refreshToken"

                        val intent = Intent(this@MainActivity, MainPage::class.java).apply{
                            putExtra("USER_ID", userId.toString())
                            putExtra("ACCESS_TOKEN", accessToken.toString())
                            putExtra("REFRESH_TOKEN", refreshToken.toString())
                        }
                        startActivity(intent)

                    } else {
                        text.text = "Ошибка входа"
                    }
                } catch (e: Exception) {
                    text.text = "Ошибка:"
                }
            }
        }

        text.setOnClickListener{
            val intent = Intent(this@MainActivity, RegisterActivity::class.java).apply{
            }
            startActivity(intent)
        }

    }
}