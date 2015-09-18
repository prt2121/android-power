package com.prt2121.bees;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pt2121 on 9/17/15.
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    // TODO
    private List<String> mDataSet;

    public TicketAdapter(List<String> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleView.setText(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final CardView cardView;

        public final TextView titleView;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.ticket_card_view);
            titleView = (TextView) v.findViewById(R.id.ticket_title);
        }

    }

}
