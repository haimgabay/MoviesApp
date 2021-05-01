package com.haimgabay.moviesapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesRecyclerAdapter extends RecyclerView.Adapter<MoviesRecyclerAdapter.ViewHolder> {

    ArrayList<SingleMovieDetails> moviesArrayList;
    Context context;
    OnMovieClickedListener mOnMovieClickedListener;


    public MoviesRecyclerAdapter(ArrayList<SingleMovieDetails> moviesArrayList, Context context, OnMovieClickedListener onMovieClickedListener){
        this.moviesArrayList = moviesArrayList;
        this.context = context;
        this.mOnMovieClickedListener = onMovieClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item_for_one_movie, parent,  false);
        ViewHolder viewHolder = new ViewHolder(view, mOnMovieClickedListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(moviesArrayList.get(position).title);
        holder.imageView.setImageResource(R.drawable.sample_image);
        downloadImageFromDb(moviesArrayList.get(position).movieImageUrl, holder.imageView);
        holder.releaseDate.setText(moviesArrayList.get(position).releaseDate);
        if (moviesArrayList.get(position).isFavorite){
            holder.favoriteImageView.setImageResource(R.drawable.favorite_image);
        }
    }

    @Override
    public int getItemCount() {
        return moviesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView imageView;
        ImageView favoriteImageView;
        TextView releaseDate;
        OnMovieClickedListener onMovieClickedListener;

        public ViewHolder(@NonNull View itemView, OnMovieClickedListener onMovieClickedListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageForMovieRow);
            title = itemView.findViewById(R.id.movieTitle);
            releaseDate = itemView.findViewById(R.id.releaseDate);
            this.onMovieClickedListener = onMovieClickedListener;
            favoriteImageView = itemView.findViewById(R.id.favorite);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnMovieClickedListener.OnMovieClicked(getBindingAdapterPosition(), itemView);
        }
    }

    public interface OnMovieClickedListener{
        void OnMovieClicked(int position, View itemView);
    }

    public void downloadImageFromDb(String imageUrl, final ImageView imageView) {
                String path = "https://image.tmdb.org/t/p/w500/" + imageUrl;
                Picasso.with(context).load(path).into(imageView);
    }
}
