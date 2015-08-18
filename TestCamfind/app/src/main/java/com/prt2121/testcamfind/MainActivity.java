package com.prt2121.testcamfind;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(CamfindService.CAMFIND_URL)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();

        CamfindService service = restAdapter.create(CamfindService.class);

        postImage(service, "/sdcard/Download/img.jpg");

        getCamFindImageResponse(service, "hUMEZpOKVZDkwQa8H8vhIg");

    }

    public Subscription getCamFindImageResponse(CamfindService service, String token) {
        Observable<Response> observable =
                service.imageResponse("CloudSight " + CamfindService.CAMFIND_KEY, token);
        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        Log.d(TAG, "Response status " + response.getStatus());
                        //Log.d(TAG, "Response mime " + response.getBody().mimeType());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Response throwable " + throwable.getLocalizedMessage());
                    }
                });
    }

    /**
     * Post a photo file to CamFind API
     *
     * @param service CamfindService
     * @param path    a photo file path
     * @return rx subscription
     */
    public Subscription postImage(CamfindService service, String path) {
        TypedString locale = new TypedString(CamfindService.DEFAULT_LOCALE);
        TypedFile photoFile = new TypedFile("image/*", new File(path));
        Observable<Response> observable =
                service.imageRequest("CloudSight " + CamfindService.CAMFIND_KEY,
                        photoFile,
                        locale);

        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        Log.d(TAG, "Response status " + response.getStatus());
                        Log.d(TAG, "Response mime " + response.getBody().mimeType());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Response throwable " + throwable.getLocalizedMessage());
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // {"token":"hUMEZpOKVZDkwQa8H8vhIg","url":"//d1spq65clhrg1f.cloudfront.net/uploads/image_request/image/28/28578/28578332/img.jpg"}
    // {"status":"completed","name":"clear plastic cup with lid"}
}
