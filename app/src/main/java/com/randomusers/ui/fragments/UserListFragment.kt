package com.randomusers.ui.fragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.randomusers.R
import com.randomusers.common.AppUtil
import com.randomusers.data.viewmodel.UserViewModel
import com.randomusers.data.viewmodel.WeatherViewModel
import com.randomusers.databinding.FragmentUsersListBinding
import com.randomusers.roomdb.model.UserDataTable
import com.randomusers.roomdb.viewmodel.UserDataViewModelRoomDB
import com.randomusers.ui.adapter.RandomUsersAdapter


class UserListFragment : Fragment() {
    private var _binding: FragmentUsersListBinding? = null
    private val binding get() = _binding!!
    lateinit var mContext: Context
    lateinit var mActivity: AppCompatActivity
    private lateinit var viewModel: UserViewModel
    private lateinit var viewModelWeather: WeatherViewModel
    private lateinit var vmRoomDB: UserDataViewModelRoomDB
    private lateinit var randomUsersAdapter: RandomUsersAdapter
    private lateinit var fm: FragmentManager
    lateinit var mLayoutManager: LinearLayoutManager

    companion object {
        private var currentPageClass = 1
        private var noOfItems = 25
        private var limit = 2
    }

    var appPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        mActivity = requireActivity() as AppCompatActivity

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.visibility = View.VISIBLE
        mActivity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
        )
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        viewModelWeather = ViewModelProvider(this)[WeatherViewModel::class.java]
        vmRoomDB = ViewModelProvider(this)[UserDataViewModelRoomDB::class.java]

        viewModel.loadData(noOfItems, currentPageClass)
        viewModelWeather.refresh(AppUtil.getSharPref(mContext, AppUtil.LAT_LONG).toString())
        fm = requireActivity().supportFragmentManager
        mLayoutManager = LinearLayoutManager(mContext)
        randomUsersAdapter = RandomUsersAdapter(fm, arrayListOf())

        binding.animationView.setAnimation(R.raw.weather_icon)
        binding.animationView.loop(true)
        binding.animationView.playAnimation()

        AppUtil.getCurrentLocation(mContext, mActivity)

        binding.rvUsersList.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = randomUsersAdapter
        }
        binding.idNestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // on scroll change we are checking when users scroll as bottom.
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                currentPageClass++
                binding.idPBLoading.visibility = View.VISIBLE
                getDate(currentPageClass, limit)
            }
        })

        binding.edSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                randomUsersAdapter.filter.filter(newText)
                return false
            }

        })
        binding.tvTemp.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                checkRequestPermissions(appPermissions)
            } else {
                viewModelWeather.refresh(AppUtil.getSharPref(mContext, AppUtil.LAT_LONG).toString())
                observeGetWeatherDetails()
            }

        }
        if (AppUtil.isInternetOn(mContext)) {
            viewModelWeather.refresh(AppUtil.getSharPref(mContext, AppUtil.LAT_LONG).toString())
            observeGetWeatherDetails()
            observeUserList()
        } else {
            Toast.makeText(
                mContext,
                getString(R.string.internet_content),
                Toast.LENGTH_SHORT
            )
        }

        return root
    }


    private fun observeGetWeatherDetails() {
        if (!AppUtil.getSharPref(mContext, AppUtil.LAT_LONG).equals("")) {
            viewModelWeather.users.observe(viewLifecycleOwner) { current ->
                current?.let {
                    binding.tvTemp.text =
                        "Current Location Weather/Air Information: \n ${it.tempC} °C ${it.tempF} °F" +
                                "  / ${it.windKph}Kpn , ${it.windMph}Mph "
                    if (it.tempC.toString().contains("-")) {
                        binding.animationView.setAnimation(R.raw.weather_snow)
                        binding.animationView.loop(true)
                        binding.animationView.playAnimation()
                    } else {
                        binding.animationView.setAnimation(R.raw.weather_sun)
                        binding.animationView.loop(true)
                        binding.animationView.playAnimation()
                    }
                }
            }
        } else {
            binding.tvTemp.text = getString(R.string.tap_here)
        }
    }

    private fun getDate(currentPageClass: Int, limit: Int) {
        if (currentPageClass > limit) {
            binding.idPBLoading.visibility = View.GONE
        }
        viewModel.loadData(noOfItems, currentPageClass)
    }

    private fun observeUserList() {
        binding.idPBLoading.visibility = View.VISIBLE
        viewModel.users.observe(viewLifecycleOwner) { list ->
            list?.let { it ->
                binding.idPBLoading.visibility = View.GONE
                binding.rvUsersList.visibility = View.VISIBLE
                randomUsersAdapter.updateCountries(it)
                for (i in it.indices) {
                    val dob = it[i].dob!!.date?.let {
                        AppUtil.dateConvertFromTo(
                            it,
                            AppUtil.APP_DATE_TIME_FORMAT_ZONE,
                            AppUtil.DOB_DATE_FORMAT
                        )
                    }
                    vmRoomDB.insert(
                        UserDataTable(
                            "${it[i].name!!.first.toString()} ",
                            it[i].name!!.last.toString(),
                            it[i].email.toString(),
                            dob.toString(),
                            it[i].phone.toString(),
                            it[i].picture!!.large.toString()
                        )
                    )
                }
            }
        }
        viewModel.usersLoadError.observe(viewLifecycleOwner) { isError ->
            binding.tvListError.visibility = if (isError == "") View.GONE else View.VISIBLE
        }
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            isLoading?.let {
                binding.idPBLoading.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    binding.tvListError.visibility = View.GONE
                }
            }
        }
    }

    private fun checkRequestPermissions(requestPermission: Array<String>) {
        val listPermissionsNeeded: ArrayList<String> = ArrayList()
        for (perm in requestPermission) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            )
                listPermissionsNeeded.add(perm)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                mActivity,
                listPermissionsNeeded.toTypedArray(), 1
            )
            return
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            val permissionResults = HashMap<String, Int>()
            var deniedCount = 0
            for (i in grantResults.indices) {
                when {
                    grantResults[i] == PackageManager.PERMISSION_DENIED -> {
                        permissionResults[permissions[i]] = grantResults[i]
                        deniedCount++
                    }
                    grantResults[i] == PackageManager.PERMISSION_GRANTED -> {
                        when (Manifest.permission.ACCESS_COARSE_LOCATION) {
                            permissions[i] -> {
                                AppUtil.getCurrentLocation(mContext, mActivity)
                            }
                        }
                    }
                }
            }

            if (deniedCount == 0) {
                AppUtil.getCurrentLocation(mContext, mActivity)
            } else {
                for (entry in permissionResults.entries) {
                    val permName: String = entry.key
                    when {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            mActivity,
                            permName
                        ) -> {
                            AppUtil.showDialog(mContext,
                                mContext.resources.getString(R.string.this_app_needs_these_permission_to_work_seemlessly),
                                mContext.resources.getString(R.string.yes_grant_permissions),
                                { dialogInterface: DialogInterface, _: Int ->
                                    dialogInterface.dismiss()
                                    checkRequestPermissions(appPermissions)
                                }
                            ) { dialogInterface: DialogInterface, _: Int ->
                                dialogInterface.dismiss()
                            }

                        }
                        else -> {
                            AppUtil.showDialog(mContext,
                                mContext.resources.getString(R.string.allow_permission_content),
                                mContext.resources.getString(R.string.go_to_settings),
                                { dialogInterface: DialogInterface, i: Int ->
                                    dialogInterface.dismiss()
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts(
                                            "package",
                                            mContext.packageName,
                                            null
                                        )
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    mActivity.finishAffinity()
                                }
                            ) { dialogInterface: DialogInterface, _: Int ->
                                dialogInterface.dismiss()
                            }
                            break
                        }
                    }

                }
            }
        }

    }

}