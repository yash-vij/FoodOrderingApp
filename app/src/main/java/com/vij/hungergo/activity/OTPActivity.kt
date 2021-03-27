package com.vij.hungergo.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StyleRes
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vij.hungergo.R
import com.vij.hungergo.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_o_t_p.*
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONObject

class OTPActivity : AppCompatActivity() {

    lateinit var OTP :EditText
    lateinit var password :EditText
    lateinit var confirmNewPassword :EditText

    var a =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)


        OTP = findViewById(R.id.enterOTP)
        password = findViewById(R.id.newPassword)
        confirmNewPassword = findViewById(R.id.confirmPassword)

        var mobileNumber = intent.getStringExtra("mobileNumber")
        submit.setOnClickListener {
            if(checkError()){
                if(ConnectionManager().checkConnectivity(this)){

                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                    val User = JSONObject()
                    User.put("mobile_number",mobileNumber)
                    User.put("password",password.text.toString())
                    User.put("otp",OTP.text.toString())


                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST,
                        url,
                        User,
                        Response.Listener {
                            println("Response is $it")

                            val obj = it.getJSONObject("data")
                            val success = obj.getBoolean("success")
                            val successMessage = obj.getString("successMessage")

                            if(success){
                                println("response is $it")
                                Toast.makeText(this,"$successMessage",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this,LoginActivity::class.java))
                                finish()

                            }else{
                                Toast.makeText(this,"Error ",Toast.LENGTH_SHORT).show()
                            }

                        },
                        Response.ErrorListener {

                            Toast.makeText(this,"Some error occured",Toast.LENGTH_SHORT).show()

                        }){
                        override fun getHeaders(): MutableMap<String, String> {

                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "5879019d3228c2"
                            return headers
                    }
                    }
                    queue.add(jsonObjectRequest)


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


            }else{
                Toast.makeText(this,"Check credentials",Toast.LENGTH_SHORT).show()
            }
        }


        setUpToolbar()
    }
    fun setUpToolbar(){
        setSupportActionBar(OTPToolbar)
        supportActionBar?.title = "Enter OTP"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        OTPToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun checkError():Boolean{
        a= 0
        if(enterOTP.text.isBlank() ){
            enterOTP.error = "Enter OTP"

        }else{
            a++
        }
        if(newPassword.text.isBlank()){
            newPassword.error = "Enter Password"
        }else{
            a++
        }
        if(confirmNewPassword.text.isBlank()){
            confirmNewPassword.error = "Enter Password"
        }else{
            a++
        }
        if(enterOTP.text.length != 4){
            enterOTP.error = "Check OTP"
        }else{
            a++
        }
        if(newPassword.text.length <4){
            newPassword.error = "Check Password"
        }
        else{
            a++
        }
        if(confirmPassword.text.toString() != newPassword.text.toString()){
            confirmNewPassword.error = "Password did not match!"
        }else{
            a++
        }

        return a == 6
    }
}
