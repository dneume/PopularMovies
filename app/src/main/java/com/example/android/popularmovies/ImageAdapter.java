package com.example.android.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by dneum on 1/9/2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private int mNumberImages;
    private static final String TAG = ImageAdapter.class.getSimpleName();
    private String [] mData = new String[0];
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    ImageAdapter(Context context, String [] data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        boolean shouldAttachToParentImmediately = false;

        View view = mInflater.inflate(R.layout.activity_list_movies, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Log.d(TAG, msg: "#" + position);
        String animal = mData[position];
        holder.myTextView.setText(animal);
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView){
            super(itemView);
            myTextView = itemView.findViewById(R.id.list_movies);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    String getItem(int id) {
        return mData[id];
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
