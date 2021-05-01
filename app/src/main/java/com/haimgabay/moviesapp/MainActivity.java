package com.haimgabay.moviesapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements MoviesRecyclerAdapter.OnMovieClickedListener {

    OkHttpClient client = new OkHttpClient();
    int currentPage = 1;
    int totalPages = 500;
    TextView headline;
    RecyclerView recyclerView;
    private ArrayList<SingleMovieDetails> moviesArrayList;
    private ArrayList<SingleMovieDetails> favoritesList;
    private ArrayList<SingleMovieDetails> nowPlaying;
    private ArrayList<SingleMovieDetails> currentArrayList;
    MoviesRecyclerAdapter moviesRecyclerAdapter;

    private String generalUrl = "https://api.themoviedb.org/3/";
    private String genre[] = {"movie/popular", "movie/now_playing", "myFavorites"};
    private String API_KEY = "?api_key=2c46288716a18fb7aadcc2a801f3fc6b";
    private int pos = -1;
    private boolean isFirstLaunch = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        headline = findViewById(R.id.headline);
        moviesArrayList = new ArrayList<SingleMovieDetails>();
        getMoviesFromDb(genre[0]);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moviesRecyclerAdapter = new MoviesRecyclerAdapter(moviesArrayList, this,this);
        currentArrayList = moviesArrayList;
        recyclerView.setAdapter(moviesRecyclerAdapter);
    }

    private void getMoviesFromDb(final String kind) {

            while (currentPage < totalPages) {
                currentPage++;
                String url = "";
                if (kind.equals(genre[0])) {
                     url = generalUrl + kind + API_KEY + "&page=" + currentPage;
                } else{
                     url = "https://api.themoviedb.org/3/" + kind + API_KEY + "&page=" + currentPage;
                }
                final Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        String res = response.body().string();
                        JSONObject jsonObject = null;
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            try {
                                jsonObject = new JSONObject(res);
                                totalPages = (int) jsonObject.get("total_pages");
                                Log.d("TOTAL_PAGES", String.valueOf(totalPages));
                                JSONArray resultsObject = (JSONArray) jsonObject.getJSONArray("results");
                                Log.d("RESPONSE", resultsObject.toString());
                                for (int i = 0; i < resultsObject.length(); i++) {
                                    JSONObject object = resultsObject.getJSONObject(i);
                                    String id = object.getString("id");
                                    String title = object.getString("title");
                                    String releaseDate = object.getString("release_date");
                                    String imageUrl = object.getString("poster_path");
                                    if (!kind.equals(genre[0])){
                                        nowPlaying.add(new SingleMovieDetails(id, title, releaseDate, imageUrl));
                                    }else
                                        moviesArrayList.add(new SingleMovieDetails(id, title, releaseDate, imageUrl));
                                }
                                if (isFirstLaunch){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.setAdapter(moviesRecyclerAdapter);
                                            isFirstLaunch = false;
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                });
            }
            currentPage = 1;
        }
    @Override
    public void OnMovieClicked(int position, View itemView) {
        pos = position;
        Intent intent = new Intent(this, DetailedMovieActivity.class);
        intent.putExtra("movieId", currentArrayList.get(position).movieId);
        intent.putExtra("position", position);
        intent.putExtra("isFavorite", currentArrayList.get(position).isFavorite);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pos != -1) {
            if (currentArrayList.get(pos).isFavorite != MovieProperties.isFavorite) {
                currentArrayList.get(pos).isFavorite = MovieProperties.isFavorite;
                moviesRecyclerAdapter.notifyItemChanged(pos);
            }
        }
    }

    @SuppressLint("ResourceType")
    public void openMoviesFilterPopup(View view) {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            popup.getMenuInflater().inflate(R.layout.popup_menu_movies_view, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
//
                    if (item.getTitle().equals("My favorites")) {
                        headline.setText("My favorites");
                        favoritesList = new ArrayList<SingleMovieDetails>();
                        if (!moviesArrayList.isEmpty()) {
                            for (int i = 0; i < moviesArrayList.size(); i++) {
                                if (moviesArrayList.get(i).isFavorite) {
                                    favoritesList.add(moviesArrayList.get(i));
                                }
                            }
                            currentArrayList = favoritesList;
                            moviesRecyclerAdapter = new MoviesRecyclerAdapter(currentArrayList, MainActivity.this, MainActivity.this);
                            recyclerView.setAdapter(moviesRecyclerAdapter);
                        }
                    }
                    else if (item.getTitle().equals("Now playing")) {
                        headline.setText("Now playing");
                        nowPlaying = new ArrayList<>();
                        getMoviesFromDb(genre[1]);
                        currentArrayList = nowPlaying;
                        moviesRecyclerAdapter = new MoviesRecyclerAdapter(currentArrayList, MainActivity.this, MainActivity.this);
                        recyclerView.setAdapter(moviesRecyclerAdapter);
                    }
                    else if (item.getTitle().equals("Popular")) {
                        headline.setText("Popular");
                        currentArrayList = moviesArrayList;
                        moviesRecyclerAdapter = new MoviesRecyclerAdapter(currentArrayList,
                                MainActivity.this, MainActivity.this);
                        recyclerView.setAdapter(moviesRecyclerAdapter);
                    }
                    return true;
                }
            });
            popup.show();
        }
    }
