package com.randomusers.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.randomusers.R
import com.randomusers.common.AppUtil
import com.randomusers.databinding.FragmentUserDetailsViewBinding
import com.randomusers.data.viewmodel.WeatherViewModel
import com.squareup.picasso.Picasso

class UserDetailsViewFragmentDialog : DialogFragment() {
    private var _binding: FragmentUserDetailsViewBinding? = null
    private val binding get() = _binding!!
    private var emailId: String? = null
    private var userName: String? = null
    private var DOB: String? = null
    private var picture: String? = null
    private var phone: String? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var address: String? = null
    private lateinit var viewModel: WeatherViewModel

    lateinit var mContext: Context
    lateinit var mActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        mActivity = requireActivity() as AppCompatActivity
        arguments?.let {
            emailId = it.getString(AppUtil.EMAIL_ID)
            userName = it.getString(AppUtil.USER_NAME)
            DOB = it.getString(AppUtil.DOB)
            picture = it.getString(AppUtil.IMAGE_PATH)
            phone = it.getString(AppUtil.PHONE)
            latitude = it.getString(AppUtil.LAT)
            longitude = it.getString(AppUtil.LONG)
            address = it.getString(AppUtil.ADDRESS)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsViewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        viewModel.refresh("$latitude,$longitude".replace("-", "").trim())

        binding.animationView.setAnimation(R.raw.weather_icon)
        binding.animationView.loop(true)
        binding.animationView.playAnimation()

        binding.toolBarBack.setOnClickListener {
            dismiss()
        }
        if (picture == "") {
            binding.ivPic.setImageResource(R.drawable.ic_person)
        } else {
            Picasso.get().load(picture).into(binding.ivPic)
        }

        binding.tvUserName.text = userName
        binding.tvEmail.text = emailId
        binding.tvPhone.text = phone
        binding.tvDOB.text = DOB
        binding.tvAddress.text = address
        observeViewModel()
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(
            userName: String,
            email: String,
            DOB: String,
            picture: String?,
            phone: String,
            latitude: String?,
            longitude: String?,
            address: String
        ) =
            UserDetailsViewFragmentDialog().apply {
                arguments = Bundle().apply {
                    putString(AppUtil.USER_NAME, userName)
                    putString(AppUtil.EMAIL_ID, email)
                    putString(AppUtil.DOB, DOB)
                    putString(AppUtil.IMAGE_PATH, picture)
                    putString(AppUtil.PHONE, phone)
                    putString(AppUtil.LAT, latitude)
                    putString(AppUtil.LONG, longitude)
                    putString(AppUtil.ADDRESS, address)
                }
            }

    }

    override fun getTheme(): Int {
        return R.style.DialogTheme

    }
    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { current ->
            current?.let {
                binding.tvWhetherLatLong.text = "Weather/Air Information: \n" +
                        "${it.tempC} °C ${it.tempF} °F" + "/ ${it.windKph}Kpn , ${it.windMph}Mph "
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
    }
}