package com.haimgabay.moviesapp;

import android.graphics.Movie;

import java.util.ArrayList;

public class MoviesState {

    private String headline = "movie/popular";
    private int listState = 0;
//    private ArrayList<Movie> movieList


    public String getHeadline() {
        return headline;
    }
    public int getListState() {
        return listState;
    }
    public void setHeadline(String headline) {
        this.headline = headline;
    }
    public void setListState(int listState) {
        this.listState = listState;
    }

}
