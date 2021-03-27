package com.vij.hungergo.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.vij.hungergo.R
import com.vij.hungergo.activity.MyCartActivity
import com.vij.hungergo.model.MenuList
import kotlinx.android.synthetic.main.menu_single_row.view.*

class MenuRecyclerAdapter(val context :Context , val dishList: ArrayList<MenuList>,val ProceedToCart:Button,val restaurantName : String,val restaurantId :String):RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {


    var itemAddCount = 0

    var dishNames  = arrayListOf<String>()
    var dishPrice = arrayListOf<String>()
    var dishId = arrayListOf<String>()

    class MenuViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val txtDishNumber : TextView = view.dishNo
        val txtDishName : TextView = view.dishName
        val txtDishCost : TextView = view.dishCostForOne
        val btnDishAdd : Button = view.btnaddDish


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_single_row,parent,false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        var menuList = dishList[position]

       holder.btnDishAdd.setOnClickListener {

           if(holder.btnDishAdd.text == "Remove"){
               itemAddCount--
               holder.btnDishAdd.text="Add"
               holder.btnDishAdd.setBackgroundResource(R.color.colorPrimary)
               dishNames.remove(menuList.dishName)
               dishPrice.remove(menuList.dishCost)

           }else{
               itemAddCount++
               holder.btnDishAdd.text="Remove"
               holder.btnDishAdd.setBackgroundResource(R.color.buttonColor)
               dishNames.add(menuList.dishName)
               dishPrice.add(menuList.dishCost)
               dishId.add(menuList.dishId)
           }
            if(itemAddCount >0){

                ProceedToCart.visibility = View.VISIBLE

            }else{
                ProceedToCart.visibility = View.INVISIBLE
            }


        }
        holder.txtDishNumber.text =(position+1).toString()
        holder.txtDishName.text = menuList.dishName
        holder.txtDishCost.text = " Rs "+menuList.dishCost
        ProceedToCart.setOnClickListener {
            val intent = Intent(context,MyCartActivity::class.java)
            intent.putExtra("RestaurantName",restaurantName)
            intent.putExtra("RestaurantId",restaurantId)
            intent.putExtra("dishNames",dishNames)
            intent.putExtra("dishPrice",dishPrice)
            intent.putExtra("dishID",dishId)
            context.startActivity(intent)

        }


        }

    }

