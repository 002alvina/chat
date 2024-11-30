package com.example.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val accessToken = intent.getStringExtra("ACCESS_TOKEN").toString()

        val contacts = findViewById<Button>(R.id.buttonRight)
        val addButton = findViewById<Button>(R.id.buttonLeft)

        val conteiner = findViewById<LinearLayout>(R.id.chat_container)

        loadChats(conteiner)

        addButton.setOnClickListener{
            addChat()
        }

        contacts.setOnClickListener{
            val intent = Intent(this@MainPage, ContactsActivity::class.java).apply{
                putExtra("ACCESS_TOKEN", accessToken.toString())
            }
            startActivity(intent)
        }


    }
    private fun loadChats(content: LinearLayout)
    {
        content.removeAllViews()

        lifecycleScope.launch {
            try {
                val accessToken = intent.getStringExtra("ACCESS_TOKEN").toString()
                val userID = intent.getStringExtra("USER_ID").toString()
                val response = NetworkService.getChats(accessToken)

                if (response.isSuccessful) {
                    val chatlist = response.body()
                    chatlist?.forEach{ chat ->
                        val chatView = View.inflate(this@MainPage,R.layout.chat_item,null)
                        val chatText: TextView = chatView.findViewById(R.id.item_name)


                        chatText.text = "Чат: ${chat.name}"

                        chatText.setOnClickListener{
                            val intent = Intent(this@MainPage, ChatActivity::class.java).apply {
                                putExtra("USER_ID", userID)
                                putExtra("ACCESS_TOKEN", accessToken)
                                putExtra("CHAT_ID", chat.id.toString())
                            }
                            startActivity(intent)
                        }

                        content.addView(chatView)

                    }?: run {
                        val emptyTextView = TextView(this@MainPage).apply {
                            text = "Pusto"
                            textSize = 16f
                        }
                        content.addView(emptyTextView)
                    }

                } else {
                    Log.e("ContactsActivity", "Бля")
                }
            } catch (e: Exception) {
                Log.e("ContactsActivity", "Бля")
            }
        }
    }
    private fun addChat()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Создать чат")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.window_add_chat, null)
        builder.setView(dialogLayout)

        val chatname = dialogLayout.findViewById<TextView>(R.id.chat_name)
        val users = dialogLayout.findViewById<TextView>(R.id.chat_users).text.toString()

        builder.setPositiveButton("Добавить"){_, _ ->
            val usersID = users.split(",").mapNotNull {it.trim().toIntOrNull()}
            add(chatname.text.toString(), usersID)
        }
        builder.setNegativeButton("Отмена", null)
        builder.show()
    }

    private fun add(chatname: String,userID: List<Int>)
    {
        lifecycleScope.launch {
            try {
                val accessToken = intent.getStringExtra("ACCESS_TOKEN").toString()
                val response = NetworkService.addChat(false,chatname, userID, accessToken)
                if (response.isSuccessful) {
                    Log.e("ContactsActivity", "Бля")

                    val chatContainer = findViewById<LinearLayout>(R.id.chat_container)
                    loadChats(chatContainer)
                } else {
                    Log.e("ContactsActivity", "Бля")
                }
            } catch (e: Exception) {
                Log.e("ContactsActivity", "Бля")
            }
        }
    }
}