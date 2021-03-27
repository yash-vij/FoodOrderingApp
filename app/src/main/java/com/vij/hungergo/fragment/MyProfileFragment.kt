package com.vij.hungergo.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.vij.hungergo.R
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.fragment_my_profile.view.*
import java.net.Inet4Address

/**
 * A simple [Fragment] subclass.
 */
class MyProfileFragment(val profileContext : Context) : Fragment() {


    lateinit var sharedPreferences: SharedPreferences
    lateinit var profileName :TextView
    lateinit var profileMobile : TextView
    lateinit var profileEmail : TextView
    lateinit var profileAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_profile, container, false)


        sharedPreferences = profileContext.getSharedPreferences(getString(R.string.login_shared_preferences),Context.MODE_PRIVATE)

        profileName = view.profileName
        profileName.text = sharedPreferences.getString("name","")

        profileEmail = view.profileEmail
        profileEmail.text = sharedPreferences.getString("email","")

        profileMobile = view.profileMobileNumber
        profileMobile.text = sharedPreferences.getString("mobile_number","")

        profileAddress = view.profileAddress
        profileAddress.text = sharedPreferences.getString("address","")



        return view
    }

}
