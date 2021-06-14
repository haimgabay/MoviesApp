package com.haimgabay.moviesapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MoviesViewModel extends ViewModel {

    private Repository repository;
    private LiveData<ArrayList<SingleMovieDetails>> movies;
    private String[] genre = {"movie/now_playing", "movie/popular", "movie/top_rated", "movie/upcoming"};
    private String currentPlayingGenre = genre[0];
    private LiveData<String> title;

    public void init(){
        if (movies != null){
            return;
        }
        repository = Repository.getInstance();
        movies = repository.getAllMovies(genre[0]);
        title = repository.getMoviesListTitle();
    }

    public void setMoviesTitle(String title) {
        repository.setMoviesListTitle(title);
    }

    public LiveData<String> getMoviesTitle(){
        return title;
    }

    public LiveData<ArrayList<SingleMovieDetails>> getAllMovies() {
        return movies;
    }

    public void getMoviesByGenre(String genre){
        if (!currentPlayingGenre.equals(genre)) {
            currentPlayingGenre = genre;
            repository.stopLoadingMovies();
            repository.getAllMovies(genre);
        }
    }
}
