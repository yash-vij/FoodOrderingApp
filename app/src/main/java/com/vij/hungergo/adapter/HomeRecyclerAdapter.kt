package com.vij.hungergo.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import com.vij.hungergo.R
import com.vij.hungergo.activity.RestaurantListActivty
import com.vij.hungergo.activity.RestaurantMenuActivity
import com.vij.hungergo.database.RestaurantDatabase
import com.vij.hungergo.database.RestaurantEntity
import com.vij.hungergo.fragment.HomeFragment
import com.vij.hungergo.model.Restaurants
import kotlinx.android.synthetic.main.recycler_home_fragment_single_row.view.*

class HomeRecyclerAdapter(val context:Context , val itemList : ArrayList<Restaurants>) :RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_fragment_single_row,parent,false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val restaurant = itemList[position]

        val restaurantEntity = RestaurantEntity(restaurant.resModelId, restaurant.resModelName,
            restaurant.resModelRating,restaurant.resModelPrice,restaurant.resModelImage)

        holder.txtRestaurantName.text = restaurant.resModelName
        holder.txtRestaurantPrice.text = "₹"+restaurant.resModelPrice + "/person"
        holder.txtRestaurantRating.text=restaurant.resModelRating
        Picasso.get().load(restaurant.resModelImage).into(holder.txtRestaurantImage)
        holder.singleRowLayout.setOnClickListener{
            val intent = Intent(context , RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantID",restaurant.resModelId)
            intent.putExtra("restaurantName",restaurant.resModelName)
            context.startActivity(intent)
        }


        //checking for favourite
        val checkFav = DBAsyncTask(context , restaurantEntity,1).execute()
        val isFav = checkFav.get()
        if(isFav){
            holder.singleRowFav.background = context.resources.getDrawable(R.drawable.ic_favourite_restaurant)

        }else{
            holder.singleRowFav.background = context.resources.getDrawable(R.drawable.ic_favourite_unchecked)

        }

        //Adding to favourite and changing image of fav
        holder.singleRowFav.setOnClickListener{
            if(!DBAsyncTask(context,restaurantEntity,1).execute().get()){
                val async = DBAsyncTask(context , restaurantEntity,2).execute()
                val result = async.get()
                if(result){

                    Toast.makeText(context,"Restaurant added to favourite",Toast.LENGTH_SHORT).show()
                    holder.singleRowFav.background = context.resources.getDrawable(R.drawable.ic_favourite_restaurant)


                }else{
                    Toast.makeText(context , "Error occured while adding",Toast.LENGTH_SHORT).show()

                }

            }else{
                val async = DBAsyncTask(context , restaurantEntity,3).execute()
                val result = async.get()
                if(result){

                    Toast.makeText(context,"Restaurant removed from favourite",Toast.LENGTH_SHORT).show()
                    holder.singleRowFav.background = context.resources.getDrawable(R.drawable.ic_favourite_unchecked)


                }else{
                    Toast.makeText(context , "Error occured while removing",Toast.LENGTH_SHORT).show()

                }

            }

        }

    }


    class HomeViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val txtRestaurantName  : TextView = view.singleRowResName
        val txtRestaurantPrice : TextView = view.singleRowPrice
        val txtRestaurantRating : TextView = view.singleRowRating
        val txtRestaurantImage : ImageView = view.singleRowImage
        val singleRowLayout : CardView = view.CardViewSingleRow
        val singleRowFav : ImageView = view.singleRowFav
    }
    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode : Int) : AsyncTask<Void, Void, Boolean>(){
        override fun doInBackground(vararg p0: Void?): Boolean {

            /*
            mode 1 ->  check DB if the restaurant is favourite or not
            mode 2 -> save the restaurant in DB as favourite
            mode 3 -> Remove the restaurnat from favourite
             */

            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurants_db").build()


            when(mode){

                1 -> {

                    val restaurant : RestaurantEntity?= db.restaurantDao().getRestaurantById(restaurantEntity.resModelId.toString())
                    db.close()
                    return restaurant != null

                }
                2->{
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return  true

                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

            }

            return false
        }

    }
}