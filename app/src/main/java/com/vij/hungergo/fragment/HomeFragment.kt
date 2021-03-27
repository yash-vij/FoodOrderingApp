package com.vij.hungergo.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.RestrictionEntry
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vij.hungergo.R
import com.vij.hungergo.adapter.HomeRecyclerAdapter
import com.vij.hungergo.database.RestaurantEntity
import com.vij.hungergo.model.Restaurants
import com.vij.hungergo.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.recycler_home_fragment_single_row.*
import kotlinx.android.synthetic.main.recycler_home_fragment_single_row.view.*
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var recyclerHomeFragment:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter : HomeRecyclerAdapter

    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar : ProgressBar
     val restaurantList = arrayListOf<Restaurants>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)



        progressLayout = view.progressLayout
        progressLayout.visibility =View.VISIBLE


        recyclerHomeFragment = view.recyclerHomeFragment
        layoutManager = LinearLayoutManager(activity)


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                val obj = it.getJSONObject("data")

                try{

                    progressLayout.visibility = View.GONE
                val success = obj.getBoolean("success")
                if(success) {

                    val data = obj.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val restaurantJsonObject = data.getJSONObject(i)
                        val restaurantObject = Restaurants(
                            restaurantJsonObject.getString("id"),
                            restaurantJsonObject.getString("name"),
                            restaurantJsonObject.getString("rating"),
                            restaurantJsonObject.getString("cost_for_one"),
                            restaurantJsonObject.getString("image_url")
                        )

                        restaurantList.add(restaurantObject)
                        recyclerAdapter = HomeRecyclerAdapter(activity as Context, restaurantList)
                        recyclerHomeFragment.adapter = recyclerAdapter
                        recyclerHomeFragment.layoutManager = layoutManager




                    }
                }else{
                    Toast.makeText(activity as Context,"Some Response Error",Toast.LENGTH_SHORT).show()
                }
                }catch (e:JSONException){
                    Toast.makeText(activity as Context,"Error occured in recieving data",Toast.LENGTH_SHORT).show()

                }

                }, Response.ErrorListener {

                Toast.makeText(activity as Context , "Volley error occured",Toast.LENGTH_SHORT).show()

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "5879019d3228c2"
                        return headers

                    }

                }
            queue.add(jsonObjectRequest)




        }else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }



        return view
    }
}