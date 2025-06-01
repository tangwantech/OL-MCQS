package com.example.gceolmcqs.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gceolmcqs.AssertReader
import com.example.gceolmcqs.R
import com.example.gceolmcqs.adapters.PackagesDialogRecyclerAdapter
import com.example.gceolmcqs.datamodels.PackageFormData
import com.example.gceolmcqs.viewmodels.PackageDialogViewModel
import com.example.gceolmcqs.viewmodels.SubscriptionActivityViewModel

class PackagesDialogFragment : DialogFragment(), PackagesDialogRecyclerAdapter.ItemSelectListener {
    private lateinit var packageDialogListener: PackageDialogListener
    private lateinit var viewModel: PackageDialogViewModel
    private var rvAdapter: PackagesDialogRecyclerAdapter? = null
    private var btnNext: Button? = null

    private val subscriptionActivityViewModel by activityViewModels<SubscriptionActivityViewModel>()

    private fun initViewModel(){
        viewModel = ViewModelProvider(requireActivity())[PackageDialogViewModel::class.java]
//        val json = AssertReader.getJsonFromAssets(requireContext(), "mcq_packages.json")
//        viewModel.setPackages(json!!)
        viewModel.setPackages(subscriptionActivityViewModel.getPackageTypes())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PackageDialogListener){
            packageDialogListener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.fragment_packages_dialog, null)
        val rv: RecyclerView = dialogView.findViewById(R.id.packageRecyclerView)
        setupRecyclerView(rv)
        val dialog = AlertDialog.Builder(requireActivity()).apply {
            setTitle("Select a package")

            setPositiveButton(requireContext().resources.getString(R.string.next)){_, _ ->

                packageDialogListener.onPackageDialogNextButtonClicked()
                viewModel.clearPackages()
                dismiss()

            }
            setNegativeButton(requireContext().resources.getString(R.string.cancel)){_, _ ->
                viewModel.clearPackages()
                packageDialogListener.onPackageDialogCancelButtonClicked()
            }
            isCancelable = false
        }.create()
        dialog.setView(dialogView)
        dialog.setOnShowListener {
            btnNext = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnNext?.isEnabled = false

        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupRecyclerView(rv: RecyclerView){

        rvAdapter = PackagesDialogRecyclerAdapter(requireContext(), viewModel.getPackages(), this)
        val loMan = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        rv.layoutManager = loMan
        rv.adapter = rvAdapter
        rvAdapter?.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance(): DialogFragment = PackagesDialogFragment()
    }


    override fun onItemSelected(position: Int, isChecked: Boolean) {
        viewModel.updatePackageDataAt(position, isChecked)
        packageDialogListener.onPackageItemSelected(viewModel.getSubjectPackageAtIndex(position))
        btnNext?.isEnabled = isChecked
//        rvAdapter?.notifyItemChanged(position)
        rvAdapter?.notifyDataSetChanged()
    }

    interface PackageDialogListener{
        fun onPackageDialogNextButtonClicked()
        fun onPackageDialogCancelButtonClicked()
        fun onPackageItemSelected(packageFormData: PackageFormData)
    }
}
