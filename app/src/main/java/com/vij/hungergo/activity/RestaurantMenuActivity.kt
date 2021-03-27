package com.vij.hungergo.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vij.hungergo.R
import com.vij.hungergo.adapter.HomeRecyclerAdapter
import com.vij.hungergo.adapter.MenuRecyclerAdapter
import com.vij.hungergo.database.RestaurantDatabase
import com.vij.hungergo.database.RestaurantEntity
import com.vij.hungergo.fragment.HomeFragment
import com.vij.hungergo.model.MenuList
import com.vij.hungergo.model.Restaurants
import com.vij.hungergo.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_restaurant_list_activty.*
import kotlinx.android.synthetic.main.activity_restaurant_menu.*
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {

    var DishList = arrayListOf<MenuList>()

    lateinit var menuRecyclerView : RecyclerView
    lateinit var menuLayoutManager : RecyclerView.LayoutManager
    lateinit var menuRecyclerAdapter :  MenuRecyclerAdapter
     lateinit var restaurantName : String
     lateinit var restaurant_id :String

    lateinit var ProceedToCart : Button

    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar: ProgressBar


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        progressLayout = MenuProgressLayout

        menuRecyclerView = findViewById(R.id.menuRecyclerView)
        menuLayoutManager = LinearLayoutManager(this)

        ProceedToCart = proceedToCart

        if(intent!=null){
            restaurant_id = intent.getStringExtra("restaurantID").toString()
            restaurantName = intent.getStringExtra("restaurantName").toString()
        }else{
            finish()
            Toast.makeText(this,"Error found !!!",Toast.LENGTH_SHORT).show()
        }
        if (restaurant_id == null){
            finish()
            Toast.makeText(this,"Error found !!!",Toast.LENGTH_SHORT).show()
        }

        if(ConnectionManager().checkConnectivity(this)) {

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurant_id"

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    val obj = it.getJSONObject("data")

                    try {

                        progressLayout.visibility = View.GONE
                        val success = obj.getBoolean("success")
                        if (success) {
                            DishList.clear()
                            val data = obj.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val menuJsonObject = data.getJSONObject(i)


                                val menuObject = MenuList(
                                    menuJsonObject.getString("id"),
                                    menuJsonObject.getString("name"),
                                    menuJsonObject.getString("cost_for_one"),
                                    menuJsonObject.getString("restaurant_id")

                                )
                                DishList.add(menuObject)

                            }
                            menuRecyclerAdapter = MenuRecyclerAdapter(this, DishList,ProceedToCart,restaurantName,restaurant_id)
                            menuRecyclerView.adapter = menuRecyclerAdapter
                            menuRecyclerView.layoutManager = menuLayoutManager
                        } else {
                            Toast.makeText(this, "Some Response Error", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this, "Error occured in recieving data", Toast.LENGTH_SHORT)
                            .show()

                    }

                }, Response.ErrorListener {

                    Toast.makeText(this, "Volley error occured", Toast.LENGTH_LONG).show()

                }) {
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
            dialog.setMessage("No Internet connection found")
            dialog.setPositiveButton("Open Setting"){text,listener->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()

            }
            dialog.setNegativeButton("Exit"){text,listener->
                finishAffinity()
            }
        }



        setUpToolbar()



    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setUpToolbar(){
        setSupportActionBar(menuToolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        menuToolbar.setNavigationOnClickListener{

            onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        if (menuRecyclerAdapter.itemAddCount > 0) {
            menuRecyclerAdapter.dishPrice.clear()
            menuRecyclerAdapter.dishNames.clear()
            super.onBackPressed()
        } else {
            startActivity(Intent(this,RestaurantListActivty::class.java))
            finishAffinity()
        }
    }



}
