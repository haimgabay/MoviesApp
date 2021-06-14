package com.haimgabay.moviesapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

public class DetailedMovieActivity extends Activity {

    private String id;
    private ImageView imageView;
    private ImageView favoriteOrNot;
    private String API_KEY = "?api_key=2c46288716a18fb7aadcc2a801f3fc6b";
    private String PATH;
    String IMAGE_PATH = "https://image.tmdb.org/t/p/w500/";
    TextView title;
    TextView details;
    TextView releaseDate;
    OkHttpClient client = new OkHttpClient();
    boolean isFavorite = false;
    ArrayList<? extends SingleMovieDetails> moviesList;
    int position = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_movie);
        imageView = findViewById(R.id.bigImageView);
        id = getIntent().getStringExtra("movieId");
        PATH = "https://api.themoviedb.org/3/movie/" + id + API_KEY;
        title = findViewById(R.id.title);
        details = findViewById(R.id.movieDetails);
        releaseDate = findViewById(R.id.releaseDate);
        favoriteOrNot = findViewById(R.id.favoriteImage);
        position = getIntent().getIntExtra("position", 0);
        isFavorite = getIntent().getBooleanExtra("isFavorite", false);
        if (isFavorite){
            favoriteOrNot.setImageResource(R.drawable.favorite_image);
        }
        getSingleMovie();
    }

    private void getSingleMovie() {

                final Request request = new Request.Builder()
                        .url(PATH)
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
                                final String movieTitle = jsonObject.get("title").toString();
                                final String movieReleaseDate = jsonObject.get("release_date").toString();
                                final String movieDetails = jsonObject.get("overview").toString();
                                final String imageUrl = jsonObject.get("poster_path").toString();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        title.setText(movieTitle);
                                        details.setText(movieDetails);
                                        releaseDate.setText(movieReleaseDate);
                                        Picasso.with(DetailedMovieActivity.this).load(
                                                IMAGE_PATH + imageUrl).fit()
                                                    .into(imageView);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                });
            }

    public void favoriteOrNot(View view) {
        if (isFavorite) {
            isFavorite = false;
            favoriteOrNot.setImageResource(R.drawable.un_favorite_icon);
        }else{
            isFavorite = true;
            favoriteOrNot.setImageResource(R.drawable.favorite_image);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MovieProperties.id = id;
        MovieProperties.isFavorite = isFavorite;
        MovieProperties.position = position;
        finish();
    }
}