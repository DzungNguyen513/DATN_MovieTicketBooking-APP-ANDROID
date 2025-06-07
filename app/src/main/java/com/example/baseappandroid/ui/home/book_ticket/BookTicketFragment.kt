package com.example.baseappandroid.ui.home.book_ticket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.baseappandroid.R
import com.example.baseappandroid.base.recyclerview.BaseAdapter
import com.example.baseappandroid.base.recyclerview.HorizontalScrollingGridLayoutManager
import com.example.baseappandroid.base.ui.BaseFragment
import com.example.baseappandroid.common.recycleview_utils.GridSpacingItemDecoration
import com.example.baseappandroid.data.model.BookTicketModel
import com.example.baseappandroid.data.model.view_data_item.MovieSeatItemModel
import com.example.baseappandroid.databinding.FragmentBookTicketBinding
import com.example.baseappandroid.utils.Extensions.convertListToString
import com.example.baseappandroid.utils.Extensions.formatMoney
import com.example.baseappandroid.utils.GlobalFunction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookTicketFragment : BaseFragment<FragmentBookTicketBinding>() {
    private val listAdapter = BaseAdapter()
    private val viewModel by viewModels<BookTicketViewModel>()
    private val seats = arrayListOf<MovieSeatItemModel>()
    private val chosenSeats = arrayListOf<MovieSeatItemModel>()
    private val arg by navArgs<BookTicketFragmentArgs>()
    override fun getContentLayout(): Int {
        return R.layout.fragment_book_ticket
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navData = arg.bookTicket
        if (navData != null) {
            viewModel.getSeatsInformation(navData)
        }
        with(binding) {
            listOfSeats.apply {
                layoutManager = HorizontalScrollingGridLayoutManager(requireContext(), 12)
                adapter = listAdapter
                addItemDecoration(GridSpacingItemDecoration(12, 25, false, 0))
            }
            textBookedSeatMovieName.text = navData?.movieName.toString()
            textBookedSeatMovieDateTime.text = "${navData?.bookTime} | ${navData?.bookDate}"
            textBookedSeatMovieName.text = navData?.movieName.toString()
            imageBack.setOnClickListener {
                findNavController().navigateUp()
            }
            buttonBookSeats.setOnClickListener {
                if (chosenSeats.size > 0) {
                    val bookMovie = BookTicketModel(
                        id = "",
                        movieName = textBookedSeatMovieName.text.toString(),
                        movieId = navData?.movieId.toString(),
                        seats = chosenSeats.map { e -> e.lineText.toString() },
                        totalMoney = (chosenSeats.size * 50000).toString(),
                        bookedDate = navData?.bookDate.toString(),
                        roomId = viewModel.seatItem?.roomId.toString(),
                        bookedTime = navData?.bookTime.toString(),
                        roomName = viewModel.seatItem?.roomName.toString()
                    )
                    findNavController().navigate(
                        BookTicketFragmentDirections.actionBookTicketFragmentToSnackOrderFragment(
                            bookMovie
                        )
                    )
                } else {
                    GlobalFunction.showToast(
                        requireContext(),
                        "Vui lòng chọn ghế ngồi trước khi thanh toán"
                    )
                }
            }
        }
        observeData()
    }

    @SuppressLint("SetTextI18n")
    private fun observeData() = with(viewModel) {
        listOfSeatsResponse.observe(viewLifecycleOwner) {
            seats.clear()
            seats.addAll(it)
            listAdapter.submitList(it.toList())
        }
        chooseSeatAction.observe(viewLifecycleOwner) {
            if (it != null) {
                chosenSeats.clear()
                for (i in seats) {
                    if (i.isChosen) {
                        chosenSeats.add(i)
                    }
                }
                if (chosenSeats.size > 0) {
                    binding.textBookedSeatMovieChosenSeats.visibility = View.VISIBLE
                    binding.textBookedSeatMovieTotalMoney.visibility = View.VISIBLE
                    binding.textBookedSeatMovieChosenSeats.text =
                        "Ghế đã đặt: ${
                            chosenSeats.map { e -> e.lineText }.toString().convertListToString()
                        }"
                    binding.textBookedSeatMovieTotalMoney.text =
                        (chosenSeats.size * 50000).formatMoney()
                } else {
                    binding.textBookedSeatMovieTotalMoney.visibility = View.GONE
                    binding.textBookedSeatMovieChosenSeats.visibility = View.GONE
                }
                clearChooseSeatAction()
            }
        }
    }
}