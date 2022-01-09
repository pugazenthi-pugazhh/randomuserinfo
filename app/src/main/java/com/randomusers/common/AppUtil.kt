package com.randomusers.common

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Looper
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.randomusers.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object AppUtil {
    const val DOB_DATE_FORMAT = "dd-MMM-yyyy"
    const val APP_DATE_TIME_FORMAT_ZONE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val EMAIL_ID = "email"
    const val USER_NAME = "user_name"
    const val DOB = "dob"
    const val ADDRESS = "address"
    const val LAT = "latitude"
    const val LONG = "longitude"
    const val IMAGE_PATH = "image_path"
    const val PHONE = "phone"
    const val BASE_URL = "https://randomuser.me/"
    const val WEATHER_REPORT_BASE_URL = "http://api.weatherapi.com/"
    private const val LOCATION_DETAILS = "Location_Details"
    const val LAT_LONG = "latlong"
    const val API_KEY = "dca892fc2d534382a1461857211207"

    fun dateConvertFromTo(dataDate: String, ConvertFrom: String, ConvertTo: String): String {
        val inputFormat: java.text.DateFormat = SimpleDateFormat(ConvertFrom)
        val outputFormat: java.text.DateFormat = SimpleDateFormat(ConvertTo)
        var date = Date()
        try {
            date = inputFormat.parse(dataDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputFormat.format(date)
    }

    fun getCurrentLocation(mContext: Context, mActivity: AppCompatActivity) {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            LocationServices.getFusedLocationProviderClient(mActivity)
                .requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        LocationServices.getFusedLocationProviderClient(mActivity)
                            .removeLocationUpdates(this)
                        if (locationResult.locations.size > 0) {
                            val latestLocationIndex = locationResult.locations.size - 1
                            val strLatitude =
                                locationResult.locations[latestLocationIndex].latitude.toString()
                            val strLongitude =
                                locationResult.locations[latestLocationIndex].longitude.toString()
                            saveSharPref(mContext, LAT_LONG, "$strLatitude,$strLongitude")

                        }
                    }
                }, Looper.getMainLooper())
        }
    }

    fun showDialog(
        mContext: Context,
        msg: String?,
        positiveLabel: String?,
        positiveOnClick: DialogInterface.OnClickListener?,
        negativeOnClick: DialogInterface.OnClickListener?
    ) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("")
        builder.setCancelable(false)
        builder.setMessage(msg)
        builder.setPositiveButton(positiveLabel, positiveOnClick)
        builder.setNegativeButton("Dismiss", negativeOnClick)
        val alertDialog = builder.create()
        alertDialog.show()
        val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }


    fun saveSharPref(mContext: Context, key: String, value: String) {
        val sharedPreferences =
            mContext.getSharedPreferences(LOCATION_DETAILS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getSharPref(mContext: Context, key: String): String? {
        val sharedPreferences =
            mContext.getSharedPreferences(LOCATION_DETAILS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }
    fun isInternetOn(con: Context): Boolean {
        var chk = false
        val connectivityManager =
            con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            chk =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
        }
        return chk
    }

}