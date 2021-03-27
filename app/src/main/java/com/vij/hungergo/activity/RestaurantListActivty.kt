package com.vij.hungergo.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.room.Room
import com.vij.hungergo.R
import com.vij.hungergo.database.RestaurantDatabase
import com.vij.hungergo.database.RestaurantEntity
import com.vij.hungergo.fragment.*
import kotlinx.android.synthetic.main.activity_restaurant_list_activty.*
import kotlinx.android.synthetic.main.drawer_header.*

class RestaurantListActivty : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences
    var previousMenuItem : MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list_activty)


        sharedPreferences = getSharedPreferences(getString(R.string.login_shared_preferences),Context.MODE_PRIVATE)



        openHomeFragment()


        setUpToolbar()


        navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem!=null){
                previousMenuItem?.isChecked = false

            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it


            when(it.itemId){
                R.id.menuHome ->  {
                    openHomeFragment()



                    drawerLayout.closeDrawers()
                }
                R.id.menuMyProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,MyProfileFragment(this))
                        .commit()
                    supportActionBar?.title = "My Profile"

                    drawerLayout.closeDrawers()
                }
                R.id.menuFavouriteRestaurant -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,FavouriteRestaurantFragment())
                        .commit()
                    supportActionBar?.title = "Favourite Restaurants"

                    drawerLayout.closeDrawers()
                }
                R.id.menuOrderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title= "Order History"

                    drawerLayout.closeDrawers()
                }
                R.id.menuFaqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,FAQsFragment())
                        .commit()
                    supportActionBar?.title = "FAQs"

                    drawerLayout.closeDrawers()
                }
                R.id.menuLogout -> {
                    sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Logout")
                    dialog.setMessage("You want to Logout ?")
                    dialog.setPositiveButton("No"){text,listener ->
                                            }
                    dialog.setNegativeButton("Yes"){text,listener ->
                        startActivity(Intent(this,LoginActivity::class.java))
                        finish()

                    }
                    dialog.create()
                    dialog.show()
                }
            }



            return@setNavigationItemSelectedListener true
        }



        val actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


    }
    fun setUpToolbar(){
        setSupportActionBar(mainToolbar)
        supportActionBar?.title="All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
            drawerName.text = sharedPreferences.getString("name","")
            drawerMobileNum.text = "+91 ${sharedPreferences.getString("mobile_number","")}"
        }

        return super.onOptionsItemSelected(item)
    }
    fun openHomeFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame,HomeFragment())
            .commit()

        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.menuHome)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when(frag){
            !is HomeFragment -> openHomeFragment()

            else -> super.onBackPressed()
        }
    }

}
