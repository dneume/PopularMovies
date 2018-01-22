package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

//import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by dneum on 1/9/2018.
 */



public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>  {

    private List<MovieDetail> mMovieDetail;
    private Context mContext;
    private ItemClickListener mClickListener;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView movie_ImageView;
        private TextView movie_title_TextView;


        public ViewHolder(View itemView){
            super(itemView);
            movie_title_TextView = (TextView) itemView.findViewById(R.id.list_movies_title);
            movie_ImageView = (ImageView) itemView.findViewById(R.id.display_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
            Log.d("OnClickListner ", String.valueOf(getAdapterPosition()));
        }
    }

    public MovieAdapter(Context context, List<MovieDetail> movie) {
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

        MovieDetail mLocalMovieDetail;
        mLocalMovieDetail = mMovieDetail.get(position);
        String mMovie_title = mLocalMovieDetail.movie_original_title;
        String mMovie_complete_path = mLocalMovieDetail.movie_complete_path;
        viewHolder.movie_title_TextView.setText(mMovie_title);

        Picasso mPicasso = null;
        mPicasso.with(mContext).load(mMovie_complete_path).fit().into(viewHolder.movie_ImageView);
        //Log.d("On bind ",mMovie_title + " " + String.valueOf(position));

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
