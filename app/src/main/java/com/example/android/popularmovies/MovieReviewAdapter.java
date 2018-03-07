package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dneum on 2/4/2018.
 */

public class  MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder>{

    private List<MovieReviewDetail> mMovieReviewDetail;
    // --Commented out by Inspection (2/13/2018 11:59 AM):private ListIterator<MovieReviewDetail> mIterateMovieReview;
    private Context mContext;
    private  int mNumOfReviews = 0;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView reviewCounterView;
        public  TextView webView;
        public  TextView authorView;

         // assign the view layout
         public ViewHolder(View itemView){
             super(itemView);
             title = itemView.findViewById(R.id.display_original_movie_title);
             webView = itemView.findViewById(R.id.display_web_review);
             reviewCounterView = itemView.findViewById(R.id.display_movie_review_count);
             authorView = itemView.findViewById(R.id.display_author);
         }
     }

    public MovieReviewAdapter(Context context, List<MovieReviewDetail> MovieReviewList) {
         mContext = context;
         mMovieReviewDetail = MovieReviewList;
    }

    private Context getContext() {
            return mContext;
    }

    @Override
    public  MovieReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        boolean shouldAttachToParentImmediately = false;
        Context context = parent.getContext();
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.review_detail, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapter.ViewHolder viewHolder, int position) {
        Log.d("OnbindViewHolder", "the position");
        MovieReviewDetail mLocalMovieReviewDetail;
        mLocalMovieReviewDetail = mMovieReviewDetail.get(position);
        viewHolder.reviewCounterView.setText(mLocalMovieReviewDetail.movie_review_increment);
        viewHolder.title.setText(mLocalMovieReviewDetail.movie_original_title);
        viewHolder.webView.setText(mLocalMovieReviewDetail.movie_review);
        viewHolder.authorView.setText(mLocalMovieReviewDetail.movie_review_author);
    }

    @Override
    public int getItemCount() {
            mNumOfReviews = mMovieReviewDetail.size();
            return mNumOfReviews;
        }


//        public interface OnClickListener {
//            void onClick(View view, int position);
//        }

        // create the public interface and an instance of the custom interface
        // mCustomListener is set in the constructor of the MovieAdapte
//    public interface OnCustomItemClickListener {
//            void onCustomItemClick(View view, int position);
//        }
//    public com.example.android.popularmovies.MovieAdapter.OnCustomItemClickListener mCustomListener;

    }
