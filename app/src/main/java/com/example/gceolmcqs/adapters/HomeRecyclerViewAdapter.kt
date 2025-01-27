package com.example.gceolmcqs.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.gceolmcqs.ActivationExpiryDatesGenerator
import com.example.gceolmcqs.SubscriptionCountDownTimer
import com.example.gceolmcqs.MCQConstants
import com.example.gceolmcqs.R
import com.example.gceolmcqs.databinding.SubjectItemCardBinding
import com.example.gceolmcqs.datamodels.SubjectPackageData


class HomeRecyclerViewAdapter(
    private val context: Context,
    private var subjectPackageDataList: ArrayList<SubjectPackageData>,
    private val onHomeRecyclerItemClickListener: OnHomeRecyclerItemClickListener,
    private val onPackageExpireListener: OnPackageExpireListener

//    private val onSubscribeButtonClickListener: OnSubscribeButtonClickListener

) : RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SubjectItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
//        val titleLo: LinearLayout = view.findViewById(R.id.titleLo)
//        val tvSubjectName: TextView = view.findViewById(R.id.subjectTitleTv)
//        val tvSubjectStatus: TextView = view.findViewById(R.id.tvSubjectStatus)
//        val btnSubscribe: Button = view.findViewById(R.id.btnSubscribe)
//        val tvPackageType: TextView = view.findViewById(R.id.tvPackageType)
//        val activatedOnTv: TextView = view.findViewById(R.id.activatedOnTv)
//        val expiresOnTv: TextView = view.findViewById(R.id.expiresOnTv)
//        val btnActivateTrial: Button = view.findViewById(R.id.activateButton)
//        val activateButtonLo: LinearLayout = view.findViewById(R.id.activateButtonLo)
//        val expiresInTv: TextView = view.findViewById(R.id.expiresInTv)
//        val expireInLo: LinearLayout = view.findViewById(R.id.expireInLo)
//
//        private val layoutSubjectItem: CardView = view.findViewById(R.id.layoutSubjectNavItem)
//

        init {

            binding.layoutSubjectNavItem.setOnClickListener {
                if(subjectPackageDataList.isNotEmpty()){
                    val packageStatus = ActivationExpiryDatesGenerator().apply{}.checkExpiry(
                        subjectPackageDataList[adapterPosition].activatedOn!!,
                        subjectPackageDataList[adapterPosition].expiresOn!!
                    )
                    onHomeRecyclerItemClickListener.onSubjectItemClicked(
                        this.adapterPosition,
                        packageStatus,
                        subjectPackageDataList[adapterPosition].packageName
                    )

                }

            }

            binding.btnSubscribe.setOnClickListener {
                onHomeRecyclerItemClickListener.onSubscribeButtonClicked(adapterPosition, subjectPackageDataList[adapterPosition].subjectName!!)
            }

            binding.loBonuses.cardSpinPoints.setOnClickListener {
                onHomeRecyclerItemClickListener.onSpinPointsButtonClicked(adapterPosition, subjectPackageDataList[adapterPosition].subjectName!!)
            }


        }

    }

    fun upSubjectPackageData(subjectPackageDataList: ArrayList<SubjectPackageData>){
        this.subjectPackageDataList = subjectPackageDataList
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

            if(subjectPackageDataList[holder.adapterPosition].isPackageActive != null){
                holder.binding.activateButtonLo.visibility = View.GONE
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
                                onPackageExpireListener.onPackageExpired(holder.adapterPosition)
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

    interface OnHomeRecyclerItemClickListener {
        fun onSubjectItemClicked(position: Int, isPackageActive: Boolean?, packageName: String?)
        fun onSubscribeButtonClicked(position: Int, subjectName: String)
        fun onSpinPointsButtonClicked(position: Int, subjectName: String)
        fun onBonusTimeButtonClicked(position: Int, subjectName: String)
    }
    interface OnPackageExpireListener{
        fun onPackageExpired(index: Int)
    }

}