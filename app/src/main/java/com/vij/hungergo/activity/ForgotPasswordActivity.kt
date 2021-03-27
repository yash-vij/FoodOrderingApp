package com.vij.hungergo.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vij.hungergo.R
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.json.JSONObject
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.properties.Delegates

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var mobileNumber :EditText
    lateinit var email : EditText
    lateinit var sharedPreferences : SharedPreferences

    var a =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        sharedPreferences = getSharedPreferences(getString(R.string.login_shared_preferences),Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("forgotMobile",forgotMobile.text.toString()).apply()

        mobileNumber = findViewById(R.id.forgotMobile)
        email = findViewById(R.id.forgotEmail)

        setUpToolbar()


        btnNext.setOnClickListener {


            if(checkError()){

                val queue = Volley.newRequestQueue(this)

                val url = "http://13.235.250.119/v2/forgot_password/fetch_result "

                val User = JSONObject()
                User.put("mobile_number",mobileNumber.text)
                User.put("email",email.text)

                val jsonObjectRequest = object : JsonObjectRequest(Method.POST,
                    url,
                    User,
                    Response.Listener {
                        println("Response is $it")

                        val obj = it.getJSONObject("data")
                        val success= obj.getBoolean("success")
                        val firstTry = obj.getBoolean("first_try")
                        if(success){

                            if(firstTry){

                                val intent = Intent(this,OTPActivity::class.java)
                                intent.putExtra("mobileNumber",mobileNumber.text.toString())
                                startActivity(intent)
                                finish()
                            }else{
                                Toast.makeText(this,"Enter Previous sent OTP",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this,OTPActivity::class.java)
                                intent.putExtra("mobileNumber",mobileNumber.text.toString())
                                startActivity(intent)
                                finish()

                            }

                        }else{
                            val MessageServer = obj.getString("errorMessage")
                            Toast.makeText(this,MessageServer.toString(),Toast.LENGTH_SHORT).show()

                        }


                    },
                    Response.ErrorListener {
                        Toast.makeText(this,"Server Error",Toast.LENGTH_SHORT).show()
                    })
                {
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
        }
    }

    fun setUpToolbar(){
        setSupportActionBar(forgotToolbar)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        forgotToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    fun checkError():Boolean{
        if(mobileNumber.text.isBlank() && email.text.isBlank()){
            forgotMobile.error = "Enter mobile number"
            forgotEmail.error = "Enter Email Address"

        }else{
            a++
        }
        if(mobileNumber.text.length != 10){
            forgotMobile.error = "Wrong Mobile number"
        }else{
            a++
        }
        return a == 2
    }

}
