package com.example.gceolmcqs.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gceolmcqs.MCQConstants
import com.example.gceolmcqs.PaperActivity
import com.example.gceolmcqs.R
//import com.example.gceolmcq.activities.OnPackageExpiredListener
import com.example.gceolmcqs.adapters.ExamTypeRecyclerViewAdapter
import com.example.gceolmcqs.viewmodels.ExamTypeFragmentViewModel


class ExamTypeFragment : Fragment(), ExamTypeRecyclerViewAdapter.OnRecyclerItemClickListener {
    private lateinit var examTypeFragmentViewModel: ExamTypeFragmentViewModel
    private lateinit var onPackageExpiredListener: OnPackageExpiredListener
    private lateinit var onContentAccessDeniedListener: OnContentAccessDeniedListener

    private lateinit var rvExamTypeFragment: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnPackageExpiredListener){
            onPackageExpiredListener = context
        }
        if(context is OnContentAccessDeniedListener){
            onContentAccessDeniedListener = context
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exam_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initVieModel()

    }

    private fun initViews(view: View){
        rvExamTypeFragment = view.findViewById(R.id.rvExamTypeFragment)
    }

    private fun initVieModel(){
        examTypeFragmentViewModel = ViewModelProvider(this)[ExamTypeFragmentViewModel::class.java]
    }

    private fun setupRecyclerView(){
        val rvLayoutMan = LinearLayoutManager(requireActivity())
        rvLayoutMan.orientation = LinearLayoutManager.VERTICAL
        rvExamTypeFragment.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        rvExamTypeFragment.layoutManager = rvLayoutMan
        val subjectIndex = requireArguments().getInt(MCQConstants.SUBJECT_INDEX)
        val examTypeIndex = requireArguments().getInt(MCQConstants.EXAM_TYPE_INDEX)
        val rvAdapter = ExamTypeRecyclerViewAdapter(
            requireContext(),
            examTypeFragmentViewModel.getExamItemTitles(subjectIndex, examTypeIndex),
            this
        )
        rvExamTypeFragment.adapter = rvAdapter
        rvExamTypeFragment.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }


    companion object {
        fun newInstance(examTypeIndex: Int, subjectName: String, expiresOn: String, packageName: String, subjectIndex:Int): Fragment {
            val examFragment = ExamTypeFragment()
            val bundle = Bundle().apply {
                putString(MCQConstants.SUBJECT_NAME, subjectName)
                putInt(MCQConstants.SUBJECT_INDEX, subjectIndex)
                putInt(MCQConstants.EXAM_TYPE_INDEX, examTypeIndex)
                putString(MCQConstants.EXPIRES_ON, expiresOn)
                putString(MCQConstants.PACKAGE_NAME, packageName)
            }
            examFragment.arguments = bundle
            return examFragment
        }
    }

    override fun onRecyclerItemClick(position: Int) {

        if(!onPackageExpiredListener.onCheckIfPackageHasExpired()){
            onPackageExpiredListener.onShowPackageExpired()
        }else{

            gotoPaperActivity(position)
        }
//        gotoPaperActivity(position)

    }
    private fun gotoPaperActivity(examYearIndex: Int){
        val subjectIndex = requireArguments().getInt(MCQConstants.SUBJECT_INDEX)
        val intent = Intent(requireContext(), PaperActivity::class.java)
        intent.apply {
            putExtra(MCQConstants.SUBJECT_INDEX, subjectIndex)
            putExtra(MCQConstants.EXPIRES_ON, requireArguments().getString(MCQConstants.EXPIRES_ON))
            putExtra(MCQConstants.SUBJECT_NAME, requireArguments().getString(MCQConstants.SUBJECT_NAME))
            putExtra(MCQConstants.EXAM_TYPE_INDEX, requireArguments().getInt(MCQConstants.EXAM_TYPE_INDEX))
            putExtra(MCQConstants.EXAM_ITEM_INDEX, examYearIndex)
        }
        startActivity(intent)
    }


    interface OnPackageExpiredListener{
        fun onShowPackageExpired()
        fun onCheckIfPackageHasExpired():Boolean
    }
    interface OnContentAccessDeniedListener{
        fun onContentAccessDenied()
    }

}