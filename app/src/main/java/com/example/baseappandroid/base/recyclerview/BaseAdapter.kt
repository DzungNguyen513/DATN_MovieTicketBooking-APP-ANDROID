package com.example.baseappandroid.base.recyclerview

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.baseappandroid.R
import com.example.baseappandroid.common.CommonUtils
import com.example.baseappandroid.data.model.BillItemModel
import com.example.baseappandroid.data.model.RoomItemModel
import com.example.baseappandroid.data.model.SnackItemModel
import com.example.baseappandroid.data.model.SnackItemType
import com.example.baseappandroid.data.model.TicketStatusType
import com.example.baseappandroid.data.model.view_data_item.AdminFeatureModel
import com.example.baseappandroid.data.model.view_data_item.BoxItemModel
import com.example.baseappandroid.data.model.view_data_item.DateItemModel
import com.example.baseappandroid.data.model.view_data_item.EditShowTimeItemModel
import com.example.baseappandroid.data.model.view_data_item.HeaderTitleItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieImageItem
import com.example.baseappandroid.data.model.view_data_item.MovieImageItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieInteractType
import com.example.baseappandroid.data.model.view_data_item.MovieItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieModel
import com.example.baseappandroid.data.model.view_data_item.MovieSeatItemModel
import com.example.baseappandroid.data.model.view_data_item.MovieSeatType
import com.example.baseappandroid.data.model.view_data_item.MovieType
import com.example.baseappandroid.data.model.view_data_item.SearchItemModel
import com.example.baseappandroid.data.model.view_data_item.StatisticItemViewData
import com.example.baseappandroid.data.model.view_data_item.WeeklyMovieItemModel
import com.example.baseappandroid.data.model.view_data_item.WeeklyShowTimeItemModel
import com.example.baseappandroid.databinding.LayoutAdminFeatureItemBinding
import com.example.baseappandroid.databinding.LayoutAdminMovieItemBinding
import com.example.baseappandroid.databinding.LayoutBoxHeightItemBinding
import com.example.baseappandroid.databinding.LayoutDateItemBinding
import com.example.baseappandroid.databinding.LayoutDetailMovieImageItemBinding
import com.example.baseappandroid.databinding.LayoutEditShowTimeItemBinding
import com.example.baseappandroid.databinding.LayoutEdtSearchItemBinding
import com.example.baseappandroid.databinding.LayoutHeaderTitleItemBinding
import com.example.baseappandroid.databinding.LayoutMovieComingSoonItemBinding
import com.example.baseappandroid.databinding.LayoutMovieHistoryItemBinding
import com.example.baseappandroid.databinding.LayoutMovieImageItemBinding
import com.example.baseappandroid.databinding.LayoutMovieItemBinding
import com.example.baseappandroid.databinding.LayoutMovieListBinding
import com.example.baseappandroid.databinding.LayoutMoviePopularItemBinding
import com.example.baseappandroid.databinding.LayoutMoviePopularListBinding
import com.example.baseappandroid.databinding.LayoutMovieSeatItemBinding
import com.example.baseappandroid.databinding.LayoutMovieSeatTextItemBinding
import com.example.baseappandroid.databinding.LayoutMovieStatisticItemBinding
import com.example.baseappandroid.databinding.LayoutRoomItemBinding
import com.example.baseappandroid.databinding.LayoutSearchMovieItemBinding
import com.example.baseappandroid.databinding.LayoutSnackItemBinding
import com.example.baseappandroid.databinding.LayoutWeeklyMovieItemBinding
import com.example.baseappandroid.databinding.LayoutWeeklyMovieShowTimeItemBinding
import com.example.baseappandroid.utils.Extensions.convertListToString
import com.example.baseappandroid.utils.Extensions.formatMoney
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlin.math.roundToInt

class BaseAdapter : ListAdapter<BaseViewData, RecyclerView.ViewHolder>(BaseViewData.HomeDiffUtil) {

    private class SearchItemViewHolder(val binding: LayoutEdtSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: SearchItemModel) = with(binding) {
            inputSearch.focusable = View.NOT_FOCUSABLE
            inputSearch.setOnClickListener {
                data.interact.searchMovieAction()
            }
        }
    }

    private class BoxItemViewHolder(val binding: LayoutBoxHeightItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BoxItemModel) = with(binding) {
            viewBoxV.layoutParams.width = RecyclerView.LayoutParams.WRAP_CONTENT
            viewBoxV.layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
            viewBoxV.setBackgroundColor(ContextCompat.getColor(root.context, R.color.white))
            data.width?.let { width ->
                viewBoxV.layoutParams.width = width
            }
            data.height?.let { height ->
                viewBoxV.layoutParams.height = height
            }
            data.backgroundColor?.let { bg ->
                viewBoxV.setBackgroundColor(bg)
            }
            viewBoxV.requestLayout()
        }
    }

    private class HeaderTitleItemViewHolder(val binding: LayoutHeaderTitleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: HeaderTitleItemModel) = with(binding) {
            textTitle.text = data.title
            if (data.showForward == true) {
                imageForward.visibility = View.VISIBLE
            } else {
                imageForward.visibility = View.GONE
            }
            imageForward.setOnClickListener {
                data.interact.onForwardAction(data.title)
            }
        }
    }

    private class MovieComingSoonItemViewHolder(val binding: LayoutMovieComingSoonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieItemModel) = with(binding) {
            textMovieNameComingSoon.text = data.title
            textMovieCategoryComingSoon.text =
                data.genre.toString().convertListToString()
            Glide.with(root.context).load(data.images?.get(0).toString()).transform(
                CenterCrop(),
                RoundedCorners(20)
            ).into(imageMovieComingSoon)
            constraintMovieTypeComingSoon.setOnClickListener {
                data.interact?.clickOnMovieAction(data.id)
            }
        }
    }

    private class MoviePopularItemViewHolder(val binding: LayoutMoviePopularItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieItemModel) = with(binding) {
            textMovieName.text = data.title
            textMovieCategory.text = data.description
            Glide.with(root.context).load(data.images?.get(0).toString()).transform(
                CenterCrop(),
                RoundedCorners(20)
            ).into(imageMovie)
            constraintMovieTypePopular.setOnClickListener {
                data.interact?.clickOnMovieAction(data.id)
            }
        }
    }

    private class MovieAdminItemViewHolder(val binding: LayoutAdminMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieItemModel) = with(binding) {
            textMovieNameAdmin.text = data.title
            textMovieCategoryAdmin.text = data.description
            Glide.with(root.context).load(data.images?.get(0).toString()).transform(
                CenterCrop(),
                RoundedCorners(20)
            ).into(imageMovieAdmin)
            buttonDeleteMovie.setOnClickListener {
                data.interact?.deleteMovieAction(data.id.toString())
            }
            buttonEditMovie.setOnClickListener {
                data.interact?.editMovieAction(data.id.toString())
            }
        }
    }

    private class MovieItemViewHolder(val binding: LayoutMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieItemModel) = with(binding) {
            constraintMovieTypeNew.visibility = View.VISIBLE
            textMovieNameNew.text = data.title
            textMovieCategoryNew.text = data.genre.toString().convertListToString()
            Glide.with(root.context).load(data.images?.get(0).toString()).transform(
                CenterCrop(),
                RoundedCorners(20)
            ).into(imageMovieNew)
            constraintMovieTypeNew.setOnClickListener {
                data.interact?.clickOnMovieAction(data.id)
            }
        }
    }

    private class MovieSearchItemViewHolder(val binding: LayoutSearchMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: MovieItemModel) = with(binding) {
            Glide.with(root.context).load(data.images?.get(0).toString()).transform(
                CenterCrop(),
                RoundedCorners(20)
            ).into(imageMovieNew)
            root.setOnClickListener {
                data.interact?.clickOnMovieAction(data.id)
            }
            textMovieCategory.text = data.genre.toString().convertListToString()
            textMovieLength.text = "${data.duration ?: "N/A"} phút"
            textMovieSearchName.text = data.title ?: "N/A"
            textMovieReleaseDate.text = data.releaseDate ?: "2025"
        }
    }

    private class MovieDetailImageItemViewHolder(val binding: LayoutDetailMovieImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieImageItem) = with(binding) {
            Glide.with(root.context).load(data.image).transform(
                CenterCrop(),
                RoundedCorners(20)
            ).into(imageDetailMovieItem)
        }
    }

    private class MoviePopularListViewHolder(val binding: LayoutMoviePopularListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val listAdapter = BaseAdapter()

        init {
            with(binding) {
                viewPagerPopular.apply {
                    adapter = listAdapter
                    clipToPadding = false
                    clipChildren = false
                    offscreenPageLimit = 5
                }
                val recyclerView = viewPagerPopular.getChildAt(0) as RecyclerView
                recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    private var startX = 0f

                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        when (e.action) {
                            MotionEvent.ACTION_DOWN -> {
                                startX = e.x
                            }

                            MotionEvent.ACTION_MOVE -> {
                                val dx = e.x - startX
                                val currentPosition = viewPagerPopular.currentItem
                                val lastPosition = listAdapter.itemCount - 1
                                if (currentPosition == 0 && dx > 0) {
                                    return true
                                }
                                if (currentPosition == lastPosition && dx < 0) {
                                    return true
                                }
                            }
                        }
                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

                    }

                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                    }
                })
            }
        }

        fun bind(data: MovieModel) = with(binding) {
            val pageTranslationX = 90 // width cần show ra của 2 item 2 bên
            val compositePageTransformer = CompositePageTransformer().apply {
                addTransformer { page, position ->
                    page.translationX = -pageTranslationX * position
                    // Next line scales the item's height. You can remove it if you don't want this effect
                    page.scaleY = 1 - (0.2f * kotlin.math.abs(position))
                }
                // Bạn có thể thêm các transformer khác tại đây nếu cần
            }
            viewPagerPopular.setPageTransformer(compositePageTransformer)
            listAdapter.submitList(data.movies?.toList())
            //viewPagerPopular.setCurrentItem(1, false)
        }
    }

    private class MovieListViewHolder(val binding: LayoutMovieListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val listAdapter = BaseAdapter()

        init {
            with(binding) {
                listOfMovies.apply {
                    layoutManager =
                        LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = listAdapter
                    hasFixedSize()
                }
            }
        }

        fun bind(data: MovieModel) = with(binding) {
            if (data.listType == MovieType.comingSoon) {
                val myLinearLayoutManager = object : LinearLayoutManager(root.context) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                listOfMovies.apply {
                    layoutManager = myLinearLayoutManager
                    adapter = listAdapter
                    hasFixedSize()
                }
            }
            listAdapter.submitList(data.movies?.toList())
        }
    }

    private class MovieSeatItemViewHolder(val binding: LayoutMovieSeatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieSeatItemModel) = with(binding) {
            if (data.isBooked) {
                imageItemSeat.setImageResource(R.drawable.ic_armchair_booked)
            } else {
                if (data.isChosen) {
                    imageItemSeat.setImageResource(R.drawable.ic_armchair_red)
                } else {
                    imageItemSeat.setImageResource(R.drawable.ic_armchair)
                }
            }
            imageItemSeat.setOnClickListener {
                if (data.movieSeatType == MovieSeatType.chair && !data.isBooked) {
                    data.lineText?.let { it1 -> data.interact.chooseSeat(it1) }
                }
            }
        }
    }

    private class MovieSeatLineTextItemViewHolder(val binding: LayoutMovieSeatTextItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieSeatItemModel) = with(binding) {
            if (data.lineText != null) {
                textMovieSeatLine.text = data.lineText
            } else {
                textMovieSeatLine.text = "N/A"
            }
        }
    }

    private inner class WeeklyMovieItemViewHolder(val binding: LayoutWeeklyMovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val timeAdapter = BaseAdapter()
        var weeklyMovieItemModel = WeeklyMovieItemModel()

        @SuppressLint("SetTextI18n")
        fun bind(data: WeeklyMovieItemModel) = with(binding) {
            weeklyMovieItemModel = data
            Glide.with(root.context).load(data.movie?.posterUrl.toString()).transform(
                CenterCrop(),
                RoundedCorners(20)
            ).into(imageWeeklyMovie)
            textWeeklyMovieDescription.text =
                "${
                    data.movie?.genre.toString().convertListToString()
                } | ${data.movie?.duration.toString()} phút"
            textWeeklyMovieName.text = data.movie?.title.toString()
            listOfShowTime.apply {
                layoutManager = setUpFlexBox()
                adapter = timeAdapter
            }
            timeAdapter.submitList(data.timesModel.orEmpty().toList())
            if (adapterPosition == currentList.size - 1) {
                viewDivine.visibility = View.INVISIBLE
            } else {
                viewDivine.visibility = View.VISIBLE
            }
            if (data.movieInteractType != null && data.movieInteractType == MovieInteractType.CLIENT) {
                buttonDeleteMovie.visibility = View.GONE
                buttonEditMovie.visibility = View.GONE
            } else {
                buttonDeleteMovie.visibility = View.VISIBLE
                buttonEditMovie.visibility = View.VISIBLE
                buttonDeleteMovie.setOnClickListener {
                    data.interact?.deleteMovieShowTimeAction(data.id.toString())
                }
                buttonEditMovie.setOnClickListener {
                    data.interact?.editMovieShowTimeAction(
                        data.times.toString(),
                        data.id.toString()
                    )
                }
            }
        }

        private fun setUpFlexBox(): FlexboxLayoutManager {
            val layoutManager = FlexboxLayoutManager(binding.root.context)
            layoutManager.flexWrap = FlexWrap.WRAP
            layoutManager.flexDirection = FlexDirection.ROW
            return layoutManager
        }
    }

    private class WeeklyMovieShowTimeItemViewHolder(val binding: LayoutWeeklyMovieShowTimeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: WeeklyShowTimeItemModel) = with(binding) {
            textMovieWeeklyShowTimeItem.text = data.time
            cardMovieWeeklyShowTimeItem.setOnClickListener {
                data.interact.chooseTime(data)
            }
        }
    }

    private class DateItemViewHolder(val binding: LayoutDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DateItemModel) = with(binding) {
            textWeeklyMovieDate.text = data.dates
            textWeeklyMovieDay.text = data.days
            if (data.chosen) {
                (frameWeeklyMovieDay.background as GradientDrawable?).apply {
                    val strokeWidth = 1
                    val newStrokeColor = ContextCompat.getColor(root.context, R.color.color_primary)
                    this?.let { setStroke(strokeWidth, newStrokeColor) }
                }
                linearDate.setBackgroundColor(root.context.resources.getColor(R.color.color_primary))
            } else {
                (frameWeeklyMovieDay.background as GradientDrawable?).apply {
                    val strokeWidth = 1
                    val newStrokeColor =
                        ContextCompat.getColor(root.context, R.color.color_grey_stroke)
                    this?.let { setStroke(strokeWidth, newStrokeColor) }
                }
                linearDate.setBackgroundColor(root.context.resources.getColor(R.color.color_grey_stroke))
            }
            cardviewDateItem.setOnClickListener {
                data.interact?.onDateChosen(data.localDate)
            }
        }
    }

    private class MovieHistoryBookedViewHolder(val binding: LayoutMovieHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: BillItemModel) = with(binding) {
            Glide.with(root.context).load(data.movieImage)
                .transform(CenterCrop(), RoundedCorners(15)).into(imageMovie)
            textMovieName.text = data.movieName.toString()
            textMovieTime.text = "${data.bookedTime} | ${data.bookedDate}"
            textPhoneNumber.text = "Số điện thoại đặt vé: ${data.userPhone.toString()}"
            when (data.status) {
                TicketStatusType.CANCEL_TICKET.value -> {
                    textStatus.text = "Trạng thái vé: Đã huỷ"
                }

                TicketStatusType.CONFIRM.value -> {
                    textStatus.text = "Trạng thái vé: Đã xác nhận"
                }

                else -> {
                    textStatus.text = "Trạng thái vé: Đang chờ xác nhận"
                }
            }
            root.setOnClickListener {
                if (data.id != null) {
                    data.interact?.onBillClick(data.id.toString())
                }
            }
        }
    }

    private class AdminFeatureViewHolder(val binding: LayoutAdminFeatureItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AdminFeatureModel) = with(binding) {
            textFeature.text = data.feature.value
            imageFeature.setImageResource(data.image)
            constraintAdminFeature.setOnClickListener {
                data.interact.onFeatureClick(data.feature)
            }
        }
    }

    private class AdminRoomManagementViewHolder(val binding: LayoutRoomItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: RoomItemModel) = with(binding) {
            textRoomName.text = "Phòng: ${data.name}"
            textRoomSeats.text = "Số lượng ghế: ${data.seats}"
            cardviewRoomItem.setOnClickListener {
                data.interact?.onRoomItemClick(data)
            }
        }
    }

    private class AddEditMovieImageItemViewHolder(val binding: LayoutMovieImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: MovieImageItemModel) = with(binding) {
            val screenWidth = CommonUtils.getWidthScreen(root.context)
            imageMovie.layoutParams.apply {
                width = (screenWidth * 0.5).roundToInt()
                height = (screenWidth * 0.5).roundToInt()
            }
            if (data.imagesString != null) {
                Glide.with(root.context).load(data.imagesString)
                    .transform(CenterCrop(), RoundedCorners(15))
                    .into(imageMovie)
            } else {
                Glide.with(root.context).load(data.uri).transform(CenterCrop(), RoundedCorners(15))
                    .into(imageMovie)
            }
        }
    }

    private class EditShowTimeItemViewHolder(val binding: LayoutEditShowTimeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EditShowTimeItemModel) = with(binding) {
            textTime.text = data.time
            imageClose.setOnClickListener {
                data.interact?.removeShowTime(data.time)
            }
        }
    }

    private class StatisticItemViewHolder(val binding: LayoutMovieStatisticItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: StatisticItemViewData) = with(binding) {
            Glide.with(root.context).load(data.movieImage)
                .transform(CenterCrop(), RoundedCorners(15)).into(imageMovie)
            textMovieName.text = data.movieName
            textMovieTime.text = "Số lượng vé: ${data.numberOfTickets}"
            textPhoneNumber.text = "Doanh thu: ${data.income}"
        }
    }

    private class SnackOrderItemViewHolder(val binding: LayoutSnackItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SnackItemModel) = with(binding) {
            textSnackName.text = data.name
            textSnackPrice.text = (data.price ?: 0.0).formatMoney()
            textTotalCount.text = data.chosenCount.toString()
            Glide.with(root.context).load(data.image).transform(CenterCrop(), RoundedCorners(15))
                .into(imageSnack)
            root.setOnClickListener {
                data.interact?.showSnackDetail(data)
            }
            if (data.type == SnackItemType.CLIENT) {
                imageMinus.visibility = View.VISIBLE
                imagePlus.visibility = View.VISIBLE
                buttonEditMovie.visibility = View.GONE
                buttonDeleteMovie.visibility = View.GONE
                imageMinus.setOnClickListener {
                    data.interact?.onMinus(data.id.toString())
                }
                imagePlus.setOnClickListener {
                    data.interact?.onPlus(data.id.toString())
                }
            } else {
                imageMinus.visibility = View.GONE
                imagePlus.visibility = View.GONE
                buttonEditMovie.visibility = View.VISIBLE
                buttonDeleteMovie.visibility = View.VISIBLE
                buttonEditMovie.setOnClickListener {
                    data.interact?.editSnack(data.id.toString())
                }
                buttonDeleteMovie.setOnClickListener {
                    data.interact?.deleteSnack(data.id.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.layout_edt_search_item -> {
                return SearchItemViewHolder(
                    LayoutEdtSearchItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_box_height_item -> {
                return BoxItemViewHolder(
                    LayoutBoxHeightItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_header_title_item -> {
                return HeaderTitleItemViewHolder(
                    LayoutHeaderTitleItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_item -> {
                return MovieItemViewHolder(
                    LayoutMovieItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_coming_soon_item -> {
                return MovieComingSoonItemViewHolder(
                    LayoutMovieComingSoonItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_popular_item -> {
                return MoviePopularItemViewHolder(
                    LayoutMoviePopularItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_admin_movie_item -> {
                return MovieAdminItemViewHolder(
                    LayoutAdminMovieItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_search_movie_item -> {
                return MovieSearchItemViewHolder(
                    LayoutSearchMovieItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_detail_movie_image_item -> {
                return MovieDetailImageItemViewHolder(
                    LayoutDetailMovieImageItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_list -> {
                return MovieListViewHolder(
                    LayoutMovieListBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_popular_list -> {
                return MoviePopularListViewHolder(
                    LayoutMoviePopularListBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_seat_item -> {
                return MovieSeatItemViewHolder(
                    LayoutMovieSeatItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_seat_text_item -> {
                return MovieSeatLineTextItemViewHolder(
                    LayoutMovieSeatTextItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_date_item -> {
                return DateItemViewHolder(
                    LayoutDateItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_weekly_movie_item -> {
                return WeeklyMovieItemViewHolder(
                    LayoutWeeklyMovieItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_weekly_movie_show_time_item -> {
                return WeeklyMovieShowTimeItemViewHolder(
                    LayoutWeeklyMovieShowTimeItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_history_item -> {
                return MovieHistoryBookedViewHolder(
                    LayoutMovieHistoryItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_admin_feature_item -> {
                return AdminFeatureViewHolder(
                    LayoutAdminFeatureItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_room_item -> {
                return AdminRoomManagementViewHolder(
                    LayoutRoomItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_image_item -> {
                return AddEditMovieImageItemViewHolder(
                    LayoutMovieImageItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_edit_show_time_item -> {
                return EditShowTimeItemViewHolder(
                    LayoutEditShowTimeItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_movie_statistic_item -> {
                return StatisticItemViewHolder(
                    LayoutMovieStatisticItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            R.layout.layout_snack_item -> {
                return SnackOrderItemViewHolder(
                    LayoutSnackItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> throw Exception()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchItemViewHolder -> {
                holder.bind(currentList[position] as SearchItemModel)
            }

            is BoxItemViewHolder -> {
                holder.bind(currentList[position] as BoxItemModel)
            }

            is HeaderTitleItemViewHolder -> {
                holder.bind(currentList[position] as HeaderTitleItemModel)
            }

            is MovieListViewHolder -> {
                holder.bind(currentList[position] as MovieModel)
            }

            is MoviePopularListViewHolder -> {
                holder.bind(currentList[position] as MovieModel)
            }

            is MovieItemViewHolder -> {
                holder.bind(currentList[position] as MovieItemModel)
            }

            is MovieComingSoonItemViewHolder -> {
                holder.bind(currentList[position] as MovieItemModel)
            }

            is MoviePopularItemViewHolder -> {
                holder.bind(currentList[position] as MovieItemModel)
            }

            is MovieAdminItemViewHolder -> {
                holder.bind(currentList[position] as MovieItemModel)
            }

            is MovieSearchItemViewHolder -> {
                holder.bind(currentList[position] as MovieItemModel)
            }

            is MovieDetailImageItemViewHolder -> {
                holder.bind(currentList[position] as MovieImageItem)
            }

            is MovieSeatItemViewHolder -> {
                holder.bind(currentList[position] as MovieSeatItemModel)
            }

            is MovieSeatLineTextItemViewHolder -> {
                holder.bind(currentList[position] as MovieSeatItemModel)
            }

            is DateItemViewHolder -> {
                holder.bind(currentList[position] as DateItemModel)
            }

            is WeeklyMovieItemViewHolder -> {
                holder.bind(currentList[position] as WeeklyMovieItemModel)
            }

            is WeeklyMovieShowTimeItemViewHolder -> {
                holder.bind(currentList[position] as WeeklyShowTimeItemModel)
            }

            is MovieHistoryBookedViewHolder -> {
                holder.bind(currentList[position] as BillItemModel)
            }

            is AdminFeatureViewHolder -> {
                holder.bind(currentList[position] as AdminFeatureModel)
            }

            is AdminRoomManagementViewHolder -> {
                holder.bind(currentList[position] as RoomItemModel)
            }

            is AddEditMovieImageItemViewHolder -> {
                holder.bind(currentList[position] as MovieImageItemModel)
            }

            is EditShowTimeItemViewHolder -> {
                holder.bind(currentList[position] as EditShowTimeItemModel)
            }

            is StatisticItemViewHolder -> {
                holder.bind(currentList[position] as StatisticItemViewData)
            }

            is SnackOrderItemViewHolder -> {
                holder.bind(currentList[position] as SnackItemModel)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].layoutRes
    }

}