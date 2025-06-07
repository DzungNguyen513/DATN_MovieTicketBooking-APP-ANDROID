package com.example.baseappandroid.ui.admin.snack

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.databinding.FragmentSnackManagementBinding
import com.example.baseappandroid.utils.AdminToAddEditSnackScreenType
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SnackManagementFragment : BaseFragment<FragmentSnackManagementBinding>() {
    private val snackAdapter = BaseAdapter()
    private val viewModel by viewModels<SnackManagementViewModel>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_snack_management
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchSnacks()
        with(binding) {
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            imageAddSnack.setOnClickListener {
                findNavController().navigate(
                    SnackManagementFragmentDirections.actionSnackManagementFragmentToAddSnackFragment(
                        editType = AdminToAddEditSnackScreenType.ADD,
                        snackId = null
                    )
                )
            }
            listOfSnacks.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = snackAdapter
            }
        }
        observeData()
    }

    private fun observeData() = with(viewModel) {
        listOfSnacks.observe(viewLifecycleOwner) {
            snackAdapter.submitList(it.toList())
        }
        editSnackAction.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    SnackManagementFragmentDirections.actionSnackManagementFragmentToAddSnackFragment(
                        editType = AdminToAddEditSnackScreenType.EDIT,
                        snackId = it
                    )
                )
                clearEditSnackAction()
            }
        }
        deleteSnackAction.observe(viewLifecycleOwner) {
            if (it != null) {
                GlobalFunction.showDeleteConfirmationDialog(
                    title = "Xác nhận xoá",
                    message = "Bạn có muốn xoá vật phẩm này!",
                    id = it,
                    context = requireContext()
                ) { _ ->
                    deleteSnackConfirm(it)
                }
                clearDeleteSnackAction()
            }
        }
    }
}