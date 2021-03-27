package com.vij.hungergo.fragment

import android.content.Context
import android.net.LinkAddress
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.vij.hungergo.R
import com.vij.hungergo.adapter.FavouriteAdapter
import com.vij.hungergo.database.RestaurantDatabase
import com.vij.hungergo.database.RestaurantEntity
import kotlinx.android.synthetic.main.fragment_favourite_restaurant.*
import kotlinx.android.synthetic.main.fragment_favourite_restaurant.view.*

class FavouriteRestaurantFragment : Fragment() {

    lateinit var favRecyclerView: RecyclerView
    lateinit var favProgressLayout : RelativeLayout
    lateinit var favProgressBar : ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var favRecyclerAdapter :FavouriteAdapter

    var restaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_restaurant, container, false)

        favRecyclerView = view.recyclerfavourite
        favProgressLayout = view.favProgressLayout
        favProgressBar = view.favProgressBar
        layoutManager = LinearLayoutManager(activity as Context)


        restaurantList = RetrieveFavourite(activity as Context).execute().get()
        if(restaurantList == null){

        }
        if(activity!=null){

            favProgressLayout.visibility = View.GONE
            favRecyclerAdapter = FavouriteAdapter(activity as Context,restaurantList)
            favRecyclerView.adapter = favRecyclerAdapter
            favRecyclerView.layoutManager = layoutManager

        }

        return  view
    }

    //for adapter we need restaurant list which is saved in database
    class RetrieveFavourite(val context: Context) :AsyncTask<Void,Void,List<RestaurantEntity>>(){
        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {

            val db = Room.databaseBuilder(context , RestaurantDatabase::class.java , "restaurants_db" ).build()
            return db.restaurantDao().getAllRestaurants()


        }


    }




}
