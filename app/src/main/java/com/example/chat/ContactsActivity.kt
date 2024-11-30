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

class ContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val accessToken = intent.getStringExtra("ACCESS_TOKEN").toString()

        val chats = findViewById<Button>(R.id.buttonRight)
        val addButton = findViewById<Button>(R.id.buttonLeft)
        val conteiner = findViewById<LinearLayout>(R.id.contact_container)


        addButton.setOnClickListener{
            addContacts()
        }
        chats.setOnClickListener{
            val intent = Intent(this@ContactsActivity, MainPage::class.java).apply{
                putExtra("ACCESS_TOKEN", accessToken.toString())
            }
            startActivity(intent)
        }

        loadContacts(conteiner)
    }

    private fun loadContacts(content: LinearLayout)
    {
        content.removeAllViews()

        lifecycleScope.launch {
            try {
                val accessToken = intent.getStringExtra("ACCESS_TOKEN").toString()
                val userID = intent.getStringExtra("USER_ID").toString()
                val response = NetworkService.showContacts(accessToken)

                if (response.isSuccessful) {
                    val contactlist = response.body()
                    contactlist?.forEach{ contact ->
                        val contactView = View.inflate(this@ContactsActivity,R.layout.contact_item,null)
                        val contactText: TextView = contactView.findViewById(R.id.item_name)
                        val delete: Button = contactView.findViewById(R.id.delete_button)

                        contactText.text = "${contact.username}"

                        /*delete.setOnClickListener{
                            val response = NetworkService.deleteContact(accessToken,contact.id)

                        }*/

                        content.addView(contactView)

                    }?: run {
                        val emptyTextView = TextView(this@ContactsActivity).apply {
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

    private fun addContacts()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Добавить контакт")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.window_add_contact, null)
        builder.setView(dialogLayout)

        val contact_login = dialogLayout.findViewById<TextView>(R.id.contact_name)

        builder.setPositiveButton("Добавить"){_, _ ->
            add(contact_login.text.toString())
        }
        builder.setNegativeButton("Отмена", null)
        builder.show()
    }

    private fun add(contact_login: String)
    {
        lifecycleScope.launch {
            try {
                val accessToken = intent.getStringExtra("ACCESS_TOKEN").toString()
                val response = NetworkService.addContacts(contact_login,accessToken)
                if (response.isSuccessful) {
                    Log.e("ContactsActivity", "Бля")

                    val chatContainer = findViewById<LinearLayout>(R.id.contact_container)
                    loadContacts(chatContainer)
                } else {
                    Log.e("ContactsActivity", "Бля")
                }
            } catch (e: Exception) {
                Log.e("ContactsActivity", "Бля")
            }
        }
    }
}