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
 */
public class CamFind implements ICamFind {

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<CamFindToken> postImage(CamFindService service, String path) {
        TypedString locale = new TypedString(CamFindService.DEFAULT_LOCALE);
        TypedFile photoFile = new TypedFile("image/*", new File(path));
        return service.imageRequest("CloudSight " + CamFindService.CAMFIND_KEY,
                photoFile,
                locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<CamFindResult> getCamFindImageResponse(CamFindService service, String token) {
        return service.imageResponse("CloudSight " + CamFindService.CAMFIND_KEY, token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<CamFindResult> pollCamFindForStatus(final Observable<CamFindResult> result) {
        return Observable.interval(12, TimeUnit.SECONDS, Schedulers.io())
                .startWith(-1L)
                .flatMap(new Func1<Long, Observable<CamFindResult>>() {
                    @Override
                    public Observable<CamFindResult> call(Long tick) {
                        return result;
                    }
                })
                .filter(new Func1<CamFindResult, Boolean>() {
                    @Override
                    public Boolean call(CamFindResult camFindResult) {
                        return camFindResult.getStatus().equalsIgnoreCase("completed");
                    }
                })
                .take(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<CamFindResult> getCamFindResultObservable(File pictureFile, final CamFindService service) {
        return postImage(service, pictureFile.getAbsolutePath())
                .map(new Func1<CamFindToken, String>() {
                    @Override
                    public String call(CamFindToken camFindToken) {
                        return camFindToken.getToken();
                    }
                })
                .cache()
                .flatMap(new Func1<String, Observable<CamFindResult>>() {
                    @Override
                    public Observable<CamFindResult> call(String s) {
                        return getCamFindImageResponse(service, s);
                    }
                });
    }

}
