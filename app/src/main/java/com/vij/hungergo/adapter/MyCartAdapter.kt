package com.vij.hungergo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vij.hungergo.R
import kotlinx.android.synthetic.main.my_cart_single_row.view.*



class MyCartAdapter(val context:Context,val restaurantName : String , val restaurnatId :String , val DishNames : ArrayList<String>,val DishPrice : ArrayList<String>):RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {


    class MyCartViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val txtDishName : TextView = view.myCartDishName
        val txtDishPrice : TextView = view.myCartDishPrice

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_cart_single_row,parent,false)
        return MyCartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return DishNames.size
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        val dishName = DishNames[position]
        var dishPrice = DishPrice[position]

        holder.txtDishName.text = dishName
        holder.txtDishPrice.text = dishPrice

    }
}