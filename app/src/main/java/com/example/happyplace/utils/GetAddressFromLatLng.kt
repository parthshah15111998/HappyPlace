package com.example.happyplace.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import java.lang.StringBuilder
import java.util.*

class GetAddressFromLatLng (context: Context,private var latitude:Double,
private var longitude:Double): AsyncTask<Void, String, String>(){

    private val geoCoder:Geocoder= Geocoder(context, Locale.getDefault())
    private lateinit var mAddressListener: AddressListener

    override fun doInBackground(vararg p0: Void?): String {
       try {
           var addressList:List<Address>? = geoCoder.getFromLocation(latitude,longitude,1)

           if (addressList != null && addressList.isNotEmpty()){
               val address:Address=addressList[0]
               val sb = StringBuilder()
               for (i in 0..address.maxAddressLineIndex){
                   sb.append(address.getAddressLine(i)).append(" ")
               }
               sb.deleteCharAt(sb.length -1)
               return sb.toString()
           }
       }catch (e:Exception){
           e.printStackTrace()
       }
        return ""
    }

    override fun onPostExecute(returnString:String?) {

        if (returnString == null){
            mAddressListener.error()
        }else{
            mAddressListener.onAddressFound(returnString)
        }
        super.onPostExecute(returnString)

    }

    fun setAddressListener(addressListener: AddressListener){
        mAddressListener=addressListener
    }

    fun getAddress(){
        execute()
    }

    interface AddressListener{
        fun onAddressFound(address:String)
        fun error()
    }
}