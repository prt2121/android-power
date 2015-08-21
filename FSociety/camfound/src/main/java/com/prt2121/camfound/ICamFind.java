package com.prt2121.camfound;

import com.prt2121.camfound.model.CamFindResult;
import com.prt2121.camfound.model.CamFindToken;

import rx.Observable;

/**
 * Created by pt2121 on 8/19/15.
 */
public interface ICamFind {

    /**
     * Post a photo file to CamFind API.
     *
     * @param service CamFindService
     * @param path    a photo file path
     * @return an Observable of CamFind token
     */
    Observable<CamFindToken> postImage(CamFindService service, String path);

    /**
     * Make a HTTP request to CamFind to get a image recognition result.
     *
     * @param service CamFindService
     * @param token   CamFind image token from postImage
     * @return a result of CamFind image recognition
     */
    Observable<CamFindResult> getCamFindImageResponse(CamFindService service, String token);

}
