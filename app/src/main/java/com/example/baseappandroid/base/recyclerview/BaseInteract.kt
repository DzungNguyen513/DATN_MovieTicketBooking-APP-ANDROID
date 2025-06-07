package com.example.baseappandroid.base.recyclerview

import com.example.baseappandroid.data.model.BillItemInteract
import com.example.baseappandroid.data.model.RoomItemInteract
import com.example.baseappandroid.data.model.SnackItemInteract
import com.example.baseappandroid.data.model.view_data_item.AdminFeatureInteract
import com.example.baseappandroid.data.model.view_data_item.DateItemInteract
import com.example.baseappandroid.data.model.view_data_item.EditShowTimeInteract
import com.example.baseappandroid.data.model.view_data_item.HeaderTitleInteract
import com.example.baseappandroid.data.model.view_data_item.MovieModelInteract
import com.example.baseappandroid.data.model.view_data_item.MovieModelItemInteract
import com.example.baseappandroid.data.model.view_data_item.MovieSeatInteract
import com.example.baseappandroid.data.model.view_data_item.SearchItemModelInteract
import com.example.baseappandroid.data.model.view_data_item.WeeklyMovieItemInteract
import com.example.baseappandroid.data.model.view_data_item.WeeklyMovieShowTimeItemInteract

interface HomeInteract : SearchItemModelInteract, MovieModelInteract, HeaderTitleInteract,
    MovieModelItemInteract, MovieSeatInteract, DateItemInteract, WeeklyMovieItemInteract,
    WeeklyMovieShowTimeItemInteract, BillItemInteract, AdminFeatureInteract, RoomItemInteract,
    EditShowTimeInteract, SnackItemInteract