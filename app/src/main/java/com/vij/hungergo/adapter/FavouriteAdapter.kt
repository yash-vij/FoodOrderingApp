package com.vij.hungergo.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vij.hungergo.R
import com.vij.hungergo.activity.RestaurantMenuActivity
import com.vij.hungergo.database.RestaurantEntity
import kotlinx.android.synthetic.main.favourite_single_row.view.*


class FavouriteAdapter(val context:Context,val restaurantList : List<RestaurantEntity>):RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {


    class FavouriteViewHolder(view:View) :RecyclerView.ViewHolder(view){
        val txtRestaurantName  : TextView = view.favSingleRowResName
        val txtRestaurantPrice : TextView = view.favSingleRowPrice
        val txtRestaurantRating : TextView = view.favSingleRowRating
        val txtRestaurantImage : ImageView = view.favSingleRowImage
        val singleRowLayout : CardView = view.favCardView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_single_row,parent,false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {

        return restaurantList.size

    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = restaurantList[position]

        holder.txtRestaurantName.text = restaurant.resModelName
        holder.txtRestaurantPrice.text = "₹"+restaurant.resModelPrice + "/person"
        holder.txtRestaurantRating.text = restaurant.resModelRating
        Picasso.get().load(restaurant.resModelImage).into(holder.txtRestaurantImage)
        holder.singleRowLayout.setOnClickListener{
            val intent = Intent(context , RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantID",restaurant.resModelId)
            intent.putExtra("restaurantName",restaurant.resModelName)
            context.startActivity(intent)
        }


    }
}