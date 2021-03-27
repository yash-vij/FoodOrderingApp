package com.vij.hungergo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "restaurants")
data class RestaurantEntity (@PrimaryKey val resModelId:String,
                             @ColumnInfo(name = "restaurant_name") val resModelName:String,
                             @ColumnInfo(name = "restaurant_rating") val resModelRating:String,
                             @ColumnInfo(name = "price_per_person") val resModelPrice:String,
                             @ColumnInfo(name = "restaurantImage") val resModelImage:String)