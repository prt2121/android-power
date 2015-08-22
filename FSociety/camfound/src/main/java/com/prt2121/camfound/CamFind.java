package com.prt2121.camfound;

import com.prt2121.camfound.model.CamFindResult;
import com.prt2121.camfound.model.CamFindToken;

import java.io.File;
import java.util.concurrent.TimeUnit;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by pt2121 on 8/19/15.
 *
 * The class facilitates communication with CamFind API
 */
public class CamFind implements ICamFind {

    public static String mCamfindKey = "";

    public CamFind(String key) {
        mCamfindKey = key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<CamFindToken> postImage(CamFindService service, String path) {
        TypedString locale = new TypedString(CamFindService.DEFAULT_LOCALE);
        TypedFile photoFile = new TypedFile("image/*", new File(path));
        return service.imageRequest("CloudSight " + mCamfindKey,
                photoFile,
                locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<CamFindResult> getCamFindImageResponse(CamFindService service, String token) {
        return service.imageResponse("CloudSight " + mCamfindKey, token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<CamFindResult> pollCamFindForStatus(final Observable<CamFindResult> result) {
        return pollCamFindForStatus(4, result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<CamFindResult> pollCamFindForStatus(int second, final Observable<CamFindResult> result) {
        return poll(second, result, camFindResult -> camFindResult.getStatus().equalsIgnoreCase("completed"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<CamFindResult> getCamFindResultObservable(File pictureFile, final CamFindService service) {
        return postImage(service, pictureFile.getAbsolutePath())
                .map(CamFindToken::getToken)
                .cache()
                .flatMap(s -> getCamFindImageResponse(service, s));
    }

    /**
     * Poll the observable until it emits something that matches a predicate.
     *
     * @param second     interval in second
     * @param observable stream
     * @param predicate  condition
     * @param <T>        type
     * @return the first observable that match the predicate
     */
    public static <T> Observable<T> poll(int second,
            final Observable<T> observable,
            Func1<? super T, Boolean> predicate) {
        return Observable.interval(second, TimeUnit.SECONDS, Schedulers.io())
                .startWith(-1L)
                .flatMap(tick -> observable)
                .filter(predicate)
                .take(1);
    }

}
