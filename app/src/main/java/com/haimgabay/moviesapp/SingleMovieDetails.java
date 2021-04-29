package com.haimgabay.moviesapp;

public class SingleMovieDetails {

    String movieId;
    String title;
    String releaseDate;
    String movieImageUrl;
    boolean isFavorite;

public SingleMovieDetails(String movieId, String title, String releaseDate, String movieImageUrl){
    this.movieId = movieId;
    this.title = title;
    this.releaseDate = releaseDate;
    this.movieImageUrl = movieImageUrl;
    this.isFavorite = false;

}
}
