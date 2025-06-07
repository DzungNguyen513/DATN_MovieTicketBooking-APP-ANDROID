package com.example.baseappandroid.ui.admin.ticket

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentTicketManagementBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TicketManagementFragment : BaseFragment<FragmentTicketManagementBinding>() {
    private val viewModel by viewModels<TicketManagementViewModel>()
    private val listAdapter = BaseAdapter()
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    override fun getContentLayout(): Int {
        return R.layout.fragment_ticket_management
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbarTicketManagement.imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            toolbarTicketManagement.textToolbarTitle.text = "Quản lý vé"
            inputSearchForm.inputSearch.hint = "Tìm kiếm theo số điện thoại..."
            inputSearchForm.inputSearch.inputType = InputType.TYPE_CLASS_NUMBER
            inputSearchForm.inputSearch.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    searchRunnable?.let { it1 -> handler.removeCallbacks(it1) }
                    searchRunnable = Runnable {
                        viewModel.searchTickets(it.toString())
                    }
                    handler.postDelayed(searchRunnable!!, 300)
                }
            }
            listOfTickets.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = listAdapter
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        listOfTickets.observe(viewLifecycleOwner) {
            listAdapter.submitList(it.toList())
        }
        onTicketChosenAction.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    TicketManagementFragmentDirections.actionTicketManagementFragmentToAdminDetailTicketFragment(
                        it.toString()
                    )
                )
                clearTicketChosen()
            }
        }
    }
}