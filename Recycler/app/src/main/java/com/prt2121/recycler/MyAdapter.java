package com.prt2121.recycler;

import com.prt2121.recycler.widget.SquareTextView;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ptanapai on 4/28/2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private String[] mData;

    private static final int MAX = 6;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[] myDataset) {
        mData = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row2, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mData[position]);
        int alpha = position > MAX ? 255 : 255 * position / MAX;
        holder.mTextView.setBackgroundColor(Color.argb(alpha, 4, 113, 179));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.length;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public SquareTextView mTextView;
//        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
//            this.mImageView = (ImageView) v.findViewById(R.id.thumbnail);
            this.mTextView = (SquareTextView) v.findViewById(R.id.number);
        }
    }
}
