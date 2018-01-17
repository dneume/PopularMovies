package com.example.android.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dneum on 1/9/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<MovieDetail> mMovieDetail;
    private Context mContext;

    private ItemClickListener mClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView movie_title_TextView;

        public ViewHolder(View itemView){
            super(itemView);
            movie_title_TextView = itemView.findViewById(R.id.list_movies_title);
   //         itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClickListner(View view) {
//            if(mClickListener != null)
//                mClickListener.onItemClick(view, getAdapterPosition());
//        }
    }

    public MovieAdapter(Context context, ArrayList<MovieDetail> movie) {
//        this.mInflater = LayoutInflater.from(context);
//        this.mMovieDetail = movie;
        mContext = context;
        mMovieDetail = movie;
     }

     private Context getContext() {
        return mContext;
     }

    @Override
    public  MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        boolean shouldAttachToParentImmediately = false;
        Context context = parent.getContext();
        LayoutInflater mInflater = LayoutInflater.from(context);
        // inflate the custom layout
        View view = mInflater.inflate(R.layout.activity_list_movies, parent, shouldAttachToParentImmediately);
        // return a new instance of a viewholder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        String movie_title = mMovieDetail.get(position).movie_original_title;
        viewHolder.movie_title_TextView.setText(movie_title);
//        Log.d("OnBindViewHolder ",  String.valueOf(position) + " " + movie_title);
    }

    @Override
    public int getItemCount() {
        return mMovieDetail.size();
    }

    String getItem(int id) {
        return mMovieDetail.get(id).movie_id;
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
