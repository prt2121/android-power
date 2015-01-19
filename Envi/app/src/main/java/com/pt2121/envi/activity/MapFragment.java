/*
 * Copyright (c) 2015 Prat Tanapaisankit
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *   * Neither the name of The Intern nor the names of its contributors may
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE LISTED COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.pt2121.envi.activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.pt2121.envi.LocUtils;
import com.pt2121.envi.R;
import com.pt2121.envi.RecycleApp;
import com.pt2121.envi.model.Loc;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private static final String TAG = MapFragment.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private SupportMapFragment fragment;

    private Loc mLoc;

    private static final String ARG_LOC = "locations";

    private OnFragmentInteractionListener mListener;

    private static final float ZOOM = 17f;

    private static final int MAX_LOCATION = 10;

    private Subscription mSubscription;

    Func1<Location, Observable<List<Loc>>> findClosestBins
            = location -> RecycleApp.getRecycleMachine(MapFragment.this.getActivity())
            .finBin().getLocs()
            .toSortedList(
                    LocUtils.compare(mLoc.latitude, mLoc.longitude));

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameter.
     *
     * @param loc Parameter 1.
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(Loc loc) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOC, loc);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLoc = getArguments().getParcelable(ARG_LOC);
            Location mockLocation = new Location(mLoc.name);
            mockLocation.setLatitude(mLoc.latitude);
            mockLocation.setLongitude(mLoc.longitude);
            Observable<Location> mockObservable = Observable.just(mockLocation);
            mSubscription = mockObservable
                    .flatMap(findClosestBins)
                    .flatMap(Observable::from)
                    .take(MAX_LOCATION).onBackpressureBuffer()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Loc>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, e.getMessage());
                        }

                        @Override
                        public void onNext(Loc loc) {
                            if (mMap != null) {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(loc.latitude, loc.longitude))
                                        .title(loc.name)
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(175))); //HSL: 175° 100% 34%
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpMapIfNeeded(mLoc);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded(mLoc);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void setUpMapIfNeeded(Loc loc) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(loc);
            }
        }
    }

    private void setUpMap(Loc loc) {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        LatLng latLng = new LatLng(loc.latitude, loc.longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title(loc.name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
    }

}
