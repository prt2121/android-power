package com.prt2121.switz;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by pt2121 on 3/13/15.
 */
public class LocTypeAdapter extends RecyclerView.Adapter<LocTypeAdapter.ViewHolder> {

    private static final String TAG = LocTypeAdapter.class.getSimpleName();

    LocType[] mTypes;

    public LocTypeAdapter(LocType[] types) {
        mTypes = types;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.type_row_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mTypes[position].name);
        holder.mSwitchCompat.setChecked(mTypes[position].checked);
    }

    @Override
    public int getItemCount() {
        return mTypes.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextView;

        public final SwitchCompat mSwitchCompat;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.typeTextView);
            mSwitchCompat = (SwitchCompat) v.findViewById(R.id.locSwitch);
        }

    }

}
