package com.example.gceolmcqs.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gceolmcqs.ActivationExpiryDatesGenerator
import com.example.gceolmcqs.SubscriptionCountDownTimer
import com.example.gceolmcqs.MCQConstants
import com.example.gceolmcqs.R
import com.example.gceolmcqs.UsageTimer
import com.example.gceolmcqs.databinding.SubjectItemCardBinding
import com.example.gceolmcqs.datamodels.SubjectPackageData


class HomeRecyclerViewAdapter(
    private val context: Context,
    private var subjectPackageDataList: ArrayList<SubjectPackageData>,
    private val onHomeRecyclerItemListener: OnHomeRecyclerItemListener

) : RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {

    private var bonusTimeEarned: Long = 0

    inner class ViewHolder(val binding: SubjectItemCardBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

            binding.layoutSubjectNavItem.setOnClickListener {
                if(subjectPackageDataList.isNotEmpty()){
                    val packageStatus = ActivationExpiryDatesGenerator().apply{}.checkExpiry(
                        subjectPackageDataList[adapterPosition].activatedOn!!,
                        subjectPackageDataList[adapterPosition].expiresOn!!
                    )
                    onHomeRecyclerItemListener.onSubjectItemClicked(
                        this.adapterPosition,
                        packageStatus,
                        subjectPackageDataList[adapterPosition].packageName
                    )

                }

            }

            binding.btnSubscribe.setOnClickListener {
                onHomeRecyclerItemListener.onSubscribeButtonClicked(adapterPosition, subjectPackageDataList[adapterPosition].subjectName!!)
            }

            binding.loBonuses.btnActivateBonus.setOnClickListener {
                onHomeRecyclerItemListener.onActivateBonusButtonClicked(adapterPosition, subjectPackageDataList[adapterPosition].subjectName!!)
            }

        }

    }

    fun upSubjectPackageData(subjectPackageDataList: ArrayList<SubjectPackageData>){
        this.subjectPackageDataList = subjectPackageDataList
    }

    fun updateBonusTime(time: Long){
        bonusTimeEarned = time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.subject_item_card, parent, false)
        val binding = SubjectItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tempPosition = position
//        println(subjectPackageDataList)
        if(subjectPackageDataList.isNotEmpty()){
            val subjectName = subjectPackageDataList[holder.adapterPosition].subjectName
            when (subjectName){
                MCQConstants.BIOLOGY -> {
//                    holder.tvSubjectName.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.biology_icon), null, null, null)
                    holder.binding.titleLo.background = context.resources.getDrawable(R.drawable.drawable_background_biology)
                    holder.binding.subjectTitleTv.setTextColor(context.resources.getColor(R.color.white))

                }
                MCQConstants.HUMAN_BIOLOGY -> {
//                    holder.tvSubjectName.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.human_biology_icon1), null, null, null)
                    holder.binding.titleLo.background = context.resources.getDrawable(R.drawable.drawable_background_human_biology)
                    holder.binding.subjectTitleTv.setTextColor(context.resources.getColor(R.color.white))

                }
            }
            holder.binding.subjectTitleTv.text = subjectName
            holder.binding.tvPackageType.text = subjectPackageDataList[holder.adapterPosition].packageName
            holder.binding.activatedOnTv.text = subjectPackageDataList[holder.adapterPosition].activatedOn
            holder.binding.expiresOnTv.text = subjectPackageDataList[holder.adapterPosition].expiresOn

            bonusTimeEarned = onHomeRecyclerItemListener.onUsageBonusAvailable(holder.adapterPosition)
            val formattedBonusTime = UsageTimer.formatToHMS(bonusTimeEarned)

            holder.binding.loBonuses.tvBonusTime.text = context.getString(R.string.bonus_time, formattedBonusTime)

            holder.binding.loBonuses.btnActivateBonus.isEnabled = UsageTimer.isBonusTimeAvailable(bonusTimeEarned)

            if(subjectPackageDataList[holder.adapterPosition].isPackageActive != null){
                holder.binding.expireInLo.visibility = View.VISIBLE
//                println("Subject package data list: ${subjectPackageDataList[position]}")
                if (subjectPackageDataList[holder.adapterPosition].isPackageActive!!){
                    holder.binding.tvSubjectStatus.text = context.resources.getString(R.string.active)
                    holder.binding.tvSubjectStatus.setTextColor(context.resources.getColor(R.color.color_green))
                    holder.binding.btnSubscribe.isEnabled = false
                    val timeLeft = ActivationExpiryDatesGenerator.getTimeRemaining(
                        subjectPackageDataList[holder.adapterPosition].activatedOn!!,
                        subjectPackageDataList[holder.adapterPosition].expiresOn!!
                    )

                    SubscriptionCountDownTimer(holder.adapterPosition).apply {
                        startTimer(timeLeft, object : SubscriptionCountDownTimer.OnTimeRemainingListener{
                            override fun onTimeRemaining(expiresIn: String) {
                                holder.binding.expiresInTv.text = expiresIn
                            }
                            override fun onExpired() {
                                holder.binding.expireInLo.visibility = View.GONE
                                holder.binding.tvSubjectStatus.text = context.resources.getString(R.string.expired)
                                holder.binding.tvSubjectStatus.setTextColor(context.resources.getColor(R.color.color_red))
                                holder.binding.btnSubscribe.isEnabled = true
                                onHomeRecyclerItemListener.onPackageExpired(holder.adapterPosition)
                            }
                        })
                    }


                } else{
                    holder.binding.expireInLo.visibility = View.GONE
                    holder.binding.tvSubjectStatus.text = context.resources.getString(R.string.expired)
                    holder.binding.tvSubjectStatus.setTextColor(context.resources.getColor(R.color.color_red))
                    holder.binding.btnSubscribe.isEnabled = true
                }
            }else{
                holder.binding.tvSubjectStatus.text = MCQConstants.NA
                holder.binding.btnSubscribe.isEnabled = false
            }
            if(subjectPackageDataList[holder.adapterPosition].packageName == context.resources.getString(R.string.trial)){
                holder.binding.btnSubscribe.isEnabled = !subjectPackageDataList[holder.adapterPosition].isPackageActive!!

            }
        }
    }

    override fun getItemCount(): Int {
        return subjectPackageDataList.size
    }

    interface OnHomeRecyclerItemListener {
        fun onSubjectItemClicked(position: Int, isPackageActive: Boolean?, packageName: String?)
        fun onSubscribeButtonClicked(position: Int, subjectName: String)
        fun onActivateBonusButtonClicked(position: Int, subjectName: String)
        fun onPackageExpired(index: Int)
        fun onUsageBonusAvailable(subjectIndex: Int): Long
    }

}