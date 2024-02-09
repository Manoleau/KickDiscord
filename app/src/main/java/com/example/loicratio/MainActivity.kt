package com.example.loicratio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : AppCompatActivity() {
    private val items:ArrayList<String> = ArrayList()
    private val itemsId:ArrayList<String> = ArrayList()
    private val spinner: Spinner by lazy { findViewById<Spinner>(R.id.spinner) }
    private lateinit var adapter:ArrayAdapter<String>
    private lateinit var db:DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DBHelper(this, null)
        findViewById<Button>(R.id.button).setOnClickListener(View.OnClickListener {
            if (spinner.selectedItem != null){
                sendMessageToDiscord(itemsId[spinner.selectedItemId.toInt()])
                Toast.makeText(this@MainActivity, "${items[spinner.selectedItemId.toInt()]} a été déco", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Rien n'est séléctionné", Toast.LENGTH_SHORT).show()
            }

        })
        updateList()
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        findViewById<Button>(R.id.buttonAdd).setOnClickListener(View.OnClickListener {
            val nom = findViewById<EditText>(R.id.editTextName)
            val idD = findViewById<EditText>(R.id.editTextId)
            var good = true
            if(nom.text.toString().isEmpty()){
                nom.error = "Veillez entrer un nom"
                good = false
            }
            if(idD.text.toString().isEmpty()){
                idD.error = "Veillez entrer une id"
                good = false
            }
            if(good){
                db.addKickDiscord(idD.text.toString(), nom.text.toString())
                Toast.makeText(this, nom.text.toString() + " a été ajouté.", Toast.LENGTH_LONG).show()
                nom.setText("")
                idD.setText("")
                updateList()
            }
        })

        findViewById<Button>(R.id.buttonSuppr).setOnClickListener(){
            if(spinner.selectedItem != null){
                db.removeKickDiscord(itemsId[spinner.selectedItemId.toInt()])
                Toast.makeText(this@MainActivity, "${items[spinner.selectedItemId.toInt()]} a été supprimé", Toast.LENGTH_SHORT).show()
                updateList()
            } else {
                Toast.makeText(this@MainActivity, "Rien n'est séléctionné", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun updateList(){
        val kickDiscords = db.getAllKickDiscord()
        items.clear()
        itemsId.clear()
        for(kickDiscord in kickDiscords){
            items.add(kickDiscord.nameDiscord)
            itemsId.add(kickDiscord.idDiscord)
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun sendMessageToDiscord(message: String) {
        val url = "https://discord.com/api/webhooks/1205572414797381722/B2OIEua7WZR2iqvUWn5mnyx0pC8jh1PKzf7tuQIK56tdBBOPkmVj1-FoGJITmZzgTruS" // Remplacez par l'URL de votre webhook
        val jsonType = "application/json; charset=utf-8".toMediaType()
        val jsonPayload = """
        {
            "content": "$message"
        }
    """.trimIndent()

        val client = OkHttpClient()
        val body = jsonPayload.toRequestBody(jsonType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        // Exécuter la requête dans un thread séparé pour ne pas bloquer le thread UI
        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Log.d("Erreur", response.toString())
                        }

                    } else {
                        runOnUiThread {
                            Log.d("Reussi", response.toString())
                        }

                    }
                    // Réussite, traiter la réponse si nécessaire
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Gérer l'exception
            }
        }.start()
    }

}