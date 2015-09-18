package com.prt2121.bees;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TicketsFragment extends Fragment {

    public TicketsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);
        RecyclerView ticketsRecyclerView = (RecyclerView) view.findViewById(R.id.ticketsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        ticketsRecyclerView.setLayoutManager(layoutManager);

        List<String> fakeDataSet = new ArrayList<>();
        fakeDataSet.add("test 1");
        fakeDataSet.add("test 2");
        fakeDataSet.add("test 3");

        TicketAdapter mAdapter = new TicketAdapter(fakeDataSet);
        ticketsRecyclerView.setAdapter(mAdapter);
        return view;
    }

}
