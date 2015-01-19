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

package com.pt2121.envi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.pt2121.envi.model.Loc;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by prt2121 on 1/19/15.
 */
public class MapUtils {

    private static final String TAG = MapUtils.class.getSimpleName();

    public static Func1<Location, Observable<List<Loc>>> getFindClosestBins(
            Context context, final long latitude, final long longitude) {
        return location -> RecycleApp.getRecycleMachine(context)
                .finBin().getLocs()
                .toSortedList(LocUtils.compare(latitude, longitude));
    }

    /**
     * Show the pins on the map.
     *
     * @param userLocationObservable an observable of user's location
     * @param findClosest            a function that returns an observable of a sorted list of
     *                               {@code Loc}
     * @param maxLocation            a maximum number of pins
     */
    public static Subscription showPins(Observable<Location> userLocationObservable,
            Func1<Location, Observable<List<Loc>>> findClosest,
            GoogleMap map,
            int maxLocation) {
        return userLocationObservable
                .flatMap(findClosest)
                .flatMap(Observable::from)
                .take(maxLocation).onBackpressureBuffer()
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
                        if (map != null) {
                            map.addMarker(new MarkerOptions()
                                    .position(new LatLng(loc.latitude, loc.longitude))
                                    .title(loc.name)
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(175))); //HSL: 175° 100% 34%
                        }
                    }
                });
    }

}
