package com.vij.hungergo.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vij.hungergo.R
import com.vij.hungergo.adapter.MyCartAdapter
import com.vij.hungergo.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_my_cart.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.properties.Delegates

class MyCartActivity : AppCompatActivity() {

    lateinit var restaurantName :String
    lateinit var restaurantId :String
    lateinit var dishName : ArrayList<String>
    lateinit var dishPrice : ArrayList<String>
    lateinit var dishId : ArrayList<String>
    var totalPrice =0
    lateinit var sharedPreferences: SharedPreferences

    lateinit var cartRecyclerView : RecyclerView
    lateinit var cartLayoutManager : RecyclerView.LayoutManager
    lateinit var cartRecyclerAdapter : MyCartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)
        cartRecyclerView = mycartRecyclerView
        cartLayoutManager = LinearLayoutManager(this)


        sharedPreferences = getSharedPreferences(getString(R.string.login_shared_preferences),
            Context.MODE_PRIVATE)

        var user_id = sharedPreferences.getString("user_id","")

        restaurantName = intent.getStringExtra("RestaurantName").toString()
        restaurantId = intent.getStringExtra("RestaurantId").toString()
        dishName = intent.getStringArrayListExtra("dishNames") as ArrayList<String>
        dishPrice = intent.getStringArrayListExtra("dishPrice") as ArrayList<String>
        dishId = intent.getStringArrayListExtra("dishID")as ArrayList<String>

        for(i in 0 until dishPrice.size){
            totalPrice += dishPrice[i].toInt()
        }
        btnPlaceOrder.text = "PLACE ORDER ($totalPrice)"

        //sending Request to server to place order

        btnPlaceOrder.setOnClickListener {

            if(ConnectionManager().checkConnectivity(this)){

                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                val array = JSONArray()
                val obj = JSONObject()

                val orderDetails = JSONObject()
                orderDetails.put("user_id",user_id)
                orderDetails.put("restaurant_id",restaurantId)
                orderDetails.put("total_cost",totalPrice)
                for(i in 0 until dishId.size){
                    obj.put("food_item_id",dishId[i])
                    array.put(obj)
                }
                orderDetails.put("food",array)


                val jsonObjectRequest = object : JsonObjectRequest(Method.POST,
                    url,
                    orderDetails,
                    Response.Listener {
                        val obj = it.getJSONObject("data")
                        val success = obj.getBoolean("success")
                        if(success){
                            println("Response is $it")
                            startActivity(Intent(this,OrderPlaced::class.java))
                            finish()
                        }else{
                            Toast.makeText(this,"No response",Toast.LENGTH_SHORT).show()
                        }

                    },
                    Response.ErrorListener {
                        println("Error is $it")
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


        cartRecyclerAdapter = MyCartAdapter(this,restaurantName,restaurantId,dishName,dishPrice)
        cartRecyclerView.adapter = cartRecyclerAdapter
        cartRecyclerView.layoutManager = cartLayoutManager
        myCartRestaurantName.text = restaurantName

        setUpToolbar()
    }
    fun setUpToolbar(){
        setSupportActionBar(myCartToolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        myCartToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        dishPrice.clear()
        dishName.clear()
        super.onBackPressed()
    }



}
