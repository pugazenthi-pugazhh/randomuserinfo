package com.randomusers.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.randomusers.R
import com.randomusers.common.AppUtil
import com.randomusers.common.AppUtil.APP_DATE_TIME_FORMAT_ZONE
import com.randomusers.common.AppUtil.DOB_DATE_FORMAT
import com.randomusers.databinding.ItemUsersListBinding
import com.randomusers.data.model.Results
import com.randomusers.ui.fragments.UserDetailsViewFragmentDialog
import com.squareup.picasso.Picasso
import java.util.*

class RandomUsersAdapter(private val fm: FragmentManager, private val list: ArrayList<Results>) :
    RecyclerView.Adapter<RandomUsersAdapter.ViewHolder>(), Filterable {
    private var mContext: Context? = null
    lateinit var binding: ItemUsersListBinding
    var filterList = ArrayList<Results>()
    init {
        filterList = list
    }

    fun updateCountries(newUsers: List<Results>) {
        list.addAll(newUsers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RandomUsersAdapter.ViewHolder {
        binding = ItemUsersListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        mContext = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RandomUsersAdapter.ViewHolder, position: Int) {
        with(holder) {
            binding.tvUserName.text =
                filterList[position].name!!.first.toString()
                    .trim() + " " + filterList[position].name!!.last.toString().trim()
            binding.tvEmail.text = filterList[position].email!!.trim()
            binding.tvAddress.text =
                filterList[position].location!!.city!!.trim() + ", " +
                        filterList[position].location!!.state!!.trim() + ", " +
                        filterList[position].location!!.country!!.trim() + ", " +
                        filterList[position].location!!.postcode!!.toString().trim()
            val dateTime = filterList[position].dob!!.date?.let {
                AppUtil.dateConvertFromTo(
                    it,
                    APP_DATE_TIME_FORMAT_ZONE,
                    DOB_DATE_FORMAT
                )
            }
            binding.tvDOB.text = dateTime
            if (filterList[position].picture!!.thumbnail == "") {
                binding.ivUserPic.setImageResource(R.drawable.ic_person)
            } else {
                Picasso.get().load(filterList[position].picture!!.thumbnail).into(binding.ivUserPic)
            }

            binding.tvView.setOnClickListener {
                UserDetailsViewFragmentDialog.newInstance(
                    binding.tvUserName.text.toString(),
                    binding.tvEmail.text.toString(),
                    binding.tvDOB.text.toString(),
                    filterList[position].picture!!.large,
                    filterList[position].phone.toString(),
                    filterList[position].location!!.coordinates!!.latitude,
                    filterList[position].location!!.coordinates!!.longitude,
                    binding.tvAddress.text.toString()
                ).show(fm, "")
            }
            holder.itemView.setOnClickListener {
                UserDetailsViewFragmentDialog.newInstance(
                    binding.tvUserName.text.toString(),
                    binding.tvEmail.text.toString(),
                    binding.tvDOB.text.toString(),
                    filterList[position].picture!!.large,
                    filterList[position].phone.toString(),
                    filterList[position].location!!.coordinates!!.latitude,
                    filterList[position].location!!.coordinates!!.longitude,
                    binding.tvAddress.text.toString()
                ).show(fm, "")
            }
        }
    }

    override fun getItemCount(): Int {
        return filterList.size

    }

    inner class ViewHolder(val binding: ItemUsersListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = list
                } else {
                    val resultList = ArrayList<Results>()
                    for (row in list) {
                        if (row.name!!.first!!.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        } else if (row.name!!.last!!.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    filterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<Results>
                notifyDataSetChanged()
            }

        }
    }
}