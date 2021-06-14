package com.haimgabay.moviesapp;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

public class Repository {

    MutableLiveData<ArrayList<SingleMovieDetails>> movies = new MutableLiveData<>();
    final ArrayList<SingleMovieDetails> currentMoviesList = new ArrayList<>();
    MutableLiveData<String> title = new MutableLiveData<>();
    OkHttpClient client = new OkHttpClient();
    private static Repository instance;
    private boolean runDownload = true;
    int currentPage = 1;
    int totalPages = 500;

    public static Repository getInstance(){
        if (instance == null){
            instance = new Repository();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<SingleMovieDetails>> getAllMovies(String genre) {
        setMoviesListTitle(genre);
        getMoviesFromDb(genre);
        return movies;
    }

    public MutableLiveData<String> getMoviesListTitle(){
        return title;
    }

    public void setMoviesListTitle(String title){
        this.title.setValue(title.substring(title.indexOf("/") + 1));
    }
    public void stopLoadingMovies(){
        runDownload = false;
    }
    public void startLoadingMovies(){
        runDownload = true;
    }

    public void getMoviesFromDb(final String genre) {
        currentPage = 0;
        startLoadingMovies();
        while ((currentPage < totalPages) && runDownload) {
            currentPage++;
            String url;
            String API_KEY = "?api_key=2c46288716a18fb7aadcc2a801f3fc6b";
            url = "https://api.themoviedb.org/3/" + genre + API_KEY + "&page=" + currentPage;
            Log.d("GENRE", url);
            final Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        String res = response.body().string();
                        JSONObject jsonObject;
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            try {
                                jsonObject = new JSONObject(res);
                                totalPages = (int) jsonObject.get("total_pages");
                                JSONArray resultsObject = (JSONArray) jsonObject.getJSONArray("results");
                                for (int i = 0; i < resultsObject.length(); i++) {
                                    JSONObject object = resultsObject.getJSONObject(i);
                                    Log.d("RESPONSE: " + genre, resultsObject.getJSONObject(i).toString());
                                    String id = object.getString("id");
                                    String title = object.getString("title");
                                    String releaseDate = object.getString("release_date");
                                    String imageUrl = object.getString("poster_path");
                                    currentMoviesList.add(new SingleMovieDetails(id, title, releaseDate, imageUrl));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            movies.setValue(currentMoviesList);
        }
    }
