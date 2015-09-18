package com.prt2121.bees;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.prt2121.bees.userlocation.IUserLocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private static final float ZOOM = 17f;

    private static final int REQUEST_ACCESS_FINE_LOCATION = 789;

    protected BehaviorSubject<Boolean> mMapReady, mPermissionGranted;

    @Inject
    IUserLocation mUserLocation;

    private GoogleMap mMap;

    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BeeApp.getInstance().getGraph().inject(this);

        RecyclerView ticketsRecyclerView = (RecyclerView) findViewById(R.id.ticketsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ticketsRecyclerView.setLayoutManager(layoutManager);
        List<String> fakeDataSet = new ArrayList<>();
        fakeDataSet.add("test 1");
        fakeDataSet.add("test 2");
        fakeDataSet.add("test 3");
        TicketAdapter mAdapter = new TicketAdapter(fakeDataSet);
        ticketsRecyclerView.setAdapter(mAdapter);

        initToolBar();
        mPermissionGranted = requestPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(this);

        mMapReady = BehaviorSubject.create(false);

        mSubscription = Observable.combineLatest(mMapReady, mPermissionGranted,
                (mapReady, permissionGranted) -> mapReady && permissionGranted)
                .first(bool -> bool)
                .flatMap(bool -> mUserLocation.locate())
                .filter(location -> location != null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
                }, throwable -> Log.e(TAG, throwable.getLocalizedMessage()));

    }

    private BehaviorSubject<Boolean> requestPermission() {
        BehaviorSubject<Boolean> subject;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int granted = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (granted != PackageManager.PERMISSION_GRANTED) {
                subject = BehaviorSubject.create(false);
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
                subject = BehaviorSubject.create(true);
            }
        } else {
            subject = BehaviorSubject.create(true);
        }
        return subject;
    }

    private void initToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUserLocation != null) {
            mUserLocation.stop();
        }
        if (mSubscription != null && mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapReady.onNext(true);
        mMapReady.onCompleted();
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = BehaviorSubject.create(true);
                } else {
                    mPermissionGranted = BehaviorSubject.create(false);
                }
                break;
            }
        }
    }

}
