package com.haimgabay.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MoviesRecyclerAdapter.OnMovieClickedListener {

    Toolbar toolbar;
    MoviesViewModel viewModel;
    RecyclerView recyclerView;
    MoviesRecyclerAdapter moviesRecyclerAdapter;
    private String[] genre = {"movie/now_playing",
            "movie/popular", "movie/top_rated", "movie/upcoming"};
    private int pos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.
                getInstance(getApplication())).get(MoviesViewModel.class);

        viewModel.init();
        viewModel.getAllMovies().observe(this, new Observer<ArrayList<SingleMovieDetails>>() {
            @Override
            public void onChanged(ArrayList<SingleMovieDetails> movies) {
                moviesRecyclerAdapter.moviesArrayList.clear();
                moviesRecyclerAdapter.moviesArrayList = movies;
                moviesRecyclerAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
            }
        });
        viewModel.getMoviesTitle().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String moviesTitle) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(moviesTitle);
            }
        });
        initRecyclerView();
    }
    private void initRecyclerView(){
        moviesRecyclerAdapter = new MoviesRecyclerAdapter(viewModel.getAllMovies().getValue(),
                this,  this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(moviesRecyclerAdapter);
    }


    @Override
    public void OnMovieClicked(int position, View itemView) {
        pos = position;
        Intent intent = new Intent(this, DetailedMovieActivity.class);
        intent.putExtra("movieId", moviesRecyclerAdapter.moviesArrayList.get(position).movieId);
        intent.putExtra("position", position);
        intent.putExtra("isFavorite", moviesRecyclerAdapter.moviesArrayList.get(position).isFavorite);
        startActivity(intent);
    }
    private void getOtherGenre(String genre){
        viewModel.getMoviesByGenre(genre);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu_movies_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nowPlaying:
                viewModel.setMoviesTitle(genre[0]);
                getOtherGenre(genre[0]);
                return true;

            case R.id.popular:
                viewModel.setMoviesTitle(genre[1]);
                getOtherGenre(genre[1]);
                return true;

            case R.id.top_rated:
                viewModel.setMoviesTitle(genre[2]);
                getOtherGenre(genre[2]);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}