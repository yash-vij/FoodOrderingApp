package com.vij.hungergo.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vij.hungergo.R
import com.vij.hungergo.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONObject


class RegistrationActivity : AppCompatActivity() {


    lateinit var sharedPreferences: SharedPreferences

    lateinit var regName :EditText
    lateinit var regEmail : EditText
    lateinit var regMobile :EditText
    lateinit var regAddress :EditText
    lateinit var regPassword : EditText
    lateinit var regConfirmPassowrd : EditText

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        setUpToolbar()

        regName = findViewById(R.id.regName)
        regEmail = findViewById(R.id.regEmail)
        regMobile = findViewById(R.id.regMobile)
        regAddress = findViewById(R.id.regDelivery)
        regPassword = findViewById(R.id.regPassword)
        regConfirmPassowrd = findViewById(R.id.regConfirmPassword)

        btnRegister.setOnClickListener {
            sharedPreferences = getSharedPreferences(
                getString(R.string.login_shared_preferences),
                Context.MODE_PRIVATE
            )

            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

            if (ConnectionManager().checkConnectivity(this)) {

                if (checkError()) {

                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val User = JSONObject()
                    User.put("name",regName.text)
                    User.put("mobile_number",regMobile.text)
                    User.put("password",regPassword.text)
                    User.put("address",regAddress.text)
                    User.put("email",regEmail.text)

                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST,
                        url,
                        User,
                        Response.Listener {
                            println("Response is $it")
                            val obj = it.getJSONObject("data")
                            val success = obj.getBoolean("success")

                            if(success){

                                val data = obj.getJSONObject("data")
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                                sharedPreferences.edit().putString("user_id",data.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name",data.getString("name")).apply()
                                sharedPreferences.edit().putString("email",data.getString("email")).apply()
                                sharedPreferences.edit().putString("mobile_number",data.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("address",data.getString("address")).apply()

                                Toast.makeText(this,"User Registered",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this,RestaurantListActivty::class.java))
                                finish()
                            }else
                            {
                                val MessageServer = obj.getString("errorMessage")
                                Toast.makeText(
                                    this,
                                    MessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this,RegistrationActivity::class.java))
                                finish()

                            }

                        },
                    Response.ErrorListener {
                        Toast.makeText(this,"Some internal error",Toast.LENGTH_SHORT).show()

                    }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "5879019d3228c2"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                }else{
                    Toast.makeText(this,"Check credentials",Toast.LENGTH_SHORT).show()
                }
            }else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("No Internet Connection")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()

            }
        }

    }
    fun setUpToolbar(){
        setSupportActionBar(regToolbar)
        supportActionBar?.title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        regToolbar.setNavigationOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
    fun checkError():Boolean{

        var a =0
        if(regName.text.isBlank()){
            regName.error = "Enter Name !"
        }else{
            a++
        }
        if(regEmail.text.isBlank()){
            regEmail.error = "Enter Email !"
        }else{
            a++
        }

        if(regMobile.text.isBlank() && regMobile.text.length!=10){
            regMobile.error = "Invalid"
        }else{
            a++
        }
        if (regAddress.text.isBlank() ){
            regEmail.error = "Enter Address"
        }else{
            a++
        }

        if (regPassword.text.isBlank() || regPassword.text.length < 4){
            regPassword.error = "Enter Password"
        }
        else{
            a++
        }
        if (regConfirmPassowrd.text.isBlank() || regConfirmPassowrd.text.length < 4){
           regConfirmPassowrd.error = "Enter Password"
        }
        else{
            a++
        }


        if (regPassword.text.isNotBlank() && regConfirmPassowrd.text.isNotBlank()) {
            if (regPassword.text.toString().toInt() == regConfirmPassowrd.text.toString().toInt()
            ) {
                a++
            } else {
                regConfirmPassowrd.error = "Password don't match"
            }
        }
        return a==7
    }

}