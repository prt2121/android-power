package com.prt2121.switz;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pt2121 on 3/13/15.
 */
public class LocTypeAdapter extends RecyclerView.Adapter<LocTypeAdapter.ViewHolder> {

    private static final String TAG = LocTypeAdapter.class.getSimpleName();

    List<LocType> mTypes;

    public LocTypeAdapter(List<LocType> types) {
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
        holder.mTextView.setText(mTypes.get(position).name);
        holder.mSwitchCompat.setChecked(mTypes.get(position).checked);
    }

    @Override
    public int getItemCount() {
        return mTypes.size();
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
