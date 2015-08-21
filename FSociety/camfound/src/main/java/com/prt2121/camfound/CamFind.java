package com.prt2121.camfound;

import com.prt2121.camfound.model.CamFindResult;
import com.prt2121.camfound.model.CamFindToken;

import java.io.File;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;

/**
 * Created by pt2121 on 8/19/15.
 */
public class CamFind implements ICamFind {

    /**
     * {@inheritDoc}
     */
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
    public Observable<CamFindResult> getCamFindImageResponse(CamFindService service, String token) {
        return service.imageResponse("CloudSight " + CamFindService.CAMFIND_KEY, token);
    }

}
