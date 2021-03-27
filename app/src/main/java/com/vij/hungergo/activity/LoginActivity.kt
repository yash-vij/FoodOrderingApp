package com.vij.hungergo.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vij.hungergo.R
import com.vij.hungergo.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {


     lateinit var sharedPreferences : SharedPreferences

    lateinit var logInMobile : EditText
    lateinit var logInPassword : EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(getString(R.string.login_shared_preferences),Context.MODE_PRIVATE)

        var isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)

        if(isLoggedIn){
            val intent = Intent(this,RestaurantListActivty::class.java)
            startActivity(intent)
            finish()
        }else{
            setContentView(R.layout.activity_login)
        }


        var intent = Intent(this,RestaurantListActivty::class.java)

        setContentView(R.layout.activity_login)
        btnLogin.setOnClickListener {

            logInMobile = findViewById(R.id.txtmobileNumber)
            logInPassword = findViewById(R.id.txtPasswordLogin)


            sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()

            if(ConnectionManager().checkConnectivity(this)){

                if(ErrorCheck()){

                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/login/fetch_result"

                    var UserLogin = JSONObject()
                    UserLogin.put("mobile_number",logInMobile.text)
                    UserLogin.put("password",logInPassword.text)

                    val jsonObjectRequest = object :JsonObjectRequest (Method.POST,
                        url,
                        UserLogin,
                        Response.Listener {
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



                                startActivity(intent)
                                finish()

                            }else{
                                Toast.makeText(this,obj.getString("errorMessage").toString(),Toast.LENGTH_SHORT).show()
                            }

                        },
                        Response.ErrorListener {

                            Toast.makeText(this,"Internal Error",Toast.LENGTH_SHORT).show()

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
                    Toast.makeText(this,"Wrong Credentials",Toast.LENGTH_SHORT).show()

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


        //sign up
        signup.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()

        }
            forgotPassword.setOnClickListener{
                val intent = Intent(this,ForgotPasswordActivity::class.java)
                startActivity(intent)
            }




    }


    fun ErrorCheck() : Boolean{

        var a =0
        if(logInMobile.text.isBlank() && logInMobile.text.length != 10){
            logInPassword.error = "Wrong Mobile Number"
        }else{
            a++
        }
        if(logInPassword.text.isBlank() && logInPassword.text.length <4){
            logInPassword.error = "Wrong Password"
        }
        else{
             a++
        }
        return a == 2
    }
}
