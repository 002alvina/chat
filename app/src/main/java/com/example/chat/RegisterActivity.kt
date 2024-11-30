package com.example.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val Rlogin = findViewById<EditText>(R.id.editTextEmailRegister)
        val Rpassword = findViewById<EditText>(R.id.editTextPasswordRegister)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val text = findViewById<TextView>(R.id.textView)

        buttonRegister.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = NetworkService.register(Rlogin.text.toString(), Rpassword.text.toString())
                    if (response.isSuccessful) {
                        text.text = "Good"

                    } else {
                        text.text = "Ошибка входа"
                    }
                } catch (e: Exception) {
                    text.text = "Ошибка:"
                }
            }
        }
    }

    /**
     * Логика регистрации пользователя
     */
    private fun handleRegistration(email: String, password: String) {
        // Пример: заглушка для регистрации
        if (email.contains("@")) {
            // Регистрация успешна
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
            finish() // Закрыть экран регистрации
        } else {
            // Показать сообщение об ошибке
            showError("Введите корректный email")
        }
    }

    /**
     * Отображение сообщения об ошибке
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
