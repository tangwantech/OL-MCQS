package com.example.gceolmcqs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gceolmcqs.databinding.ActivitySpinBinding
import com.example.gceolmcqs.databinding.DialogBonusEarnedBinding
import com.example.gceolmcqs.viewmodels.SpinActivityViewModel

class SpinActivity : AppCompatActivity() {

    private lateinit var viewModel: SpinActivityViewModel
    private lateinit var binding: ActivitySpinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpinBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initViewModel()
        setupObservers()
        setupListeners()
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[SpinActivityViewModel::class.java]
        viewModel.initSpinToEarnTime("24H")
    }

    private fun setupListeners(){
        binding.btnSpin.setOnClickListener {
            binding.btnSpin.isEnabled = false
            viewModel.spin(object : SpinToEarnTime.SpinToEarnTimeListener{
                override fun onSpinAngleChange(angle: Float) {
                    binding.pointer.rotation = angle
                }

                override fun bonusesAvailable(currentBonusEarned: Long, bonusInMinutes: Int, accruedBonus: Long) {
                    binding.loBonusEarned.root.visibility = View.VISIBLE
                    binding.loBonusEarned.tvBonus.text = getString(R.string.bonus_message, "$bonusInMinutes")
                    binding.btnSpin.isEnabled = true
                }

                override fun spinPointsRemaining(spinPoints: Int) {

                }

            })

        }

        binding.loBonusEarned.btnRedeemNow.setOnClickListener {

        }

        binding.loBonusEarned.btnRedeemLater.setOnClickListener {
            exitActivity()
        }
    }
    private fun setupObservers(){
        viewModel.packages.observe(this){
            binding.tvBonus1.text = getString(R.string.mins_tag, "${it[0]}")
            binding.tvBonus2.text = getString(R.string.mins_tag, "${it[1]}")
            binding.tvBonus3.text = getString(R.string.mins_tag, "${it[2]}")
            binding.tvBonus4.text = getString(R.string.mins_tag, "${it[3]}")
            binding.tvBonus5.text = getString(R.string.mins_tag, "${it[4]}")
            binding.tvBonus6.text = getString(R.string.mins_tag, "${it[5]}")
            binding.tvBonus7.text = getString(R.string.mins_tag, "${it[6]}")
            binding.tvBonus8.text = getString(R.string.mins_tag, "${it[7]}")
        }
    }


    private fun exitActivity(){
        finish()
    }

    companion object{
        fun getIntent(context: Context, position: Int): Intent {
            val intent = Intent(context, SpinActivity::class.java)
            intent.putExtra(MCQConstants.SUBJECT_INDEX, position)
            return intent
        }
    }
}