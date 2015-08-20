package com.prt2121.fsociety.camfind;

import com.prt2121.fsociety.camfind.model.CamFindResult;
import com.prt2121.fsociety.camfind.model.CamFindToken;

import java.io.File;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;

/**
 * Created by pt2121 on 8/19/15.
 */
public class CamFind implements ICamFind {

    public static final String TAG = CamFind.class.getSimpleName();

    /**
     * Post a photo file to CamFind API
     *
     * @param service CamFindService
     * @param path    a photo file path
     * @return rx subscription
     */
    public Observable<CamFindToken> postImage(CamFindService service, String path) {
        TypedString locale = new TypedString(CamFindService.DEFAULT_LOCALE);
        TypedFile photoFile = new TypedFile("image/*", new File(path));
        return service.imageRequest("CloudSight " + CamFindService.CAMFIND_KEY,
                photoFile,
                locale);
    }

    public Observable<CamFindResult> getCamFindImageResponse(CamFindService service, String token) {
        return service.imageResponse("CloudSight " + CamFindService.CAMFIND_KEY, token);
    }

}
