package com.prt2121.camfound;

import com.prt2121.camfound.model.CamFindResult;
import com.prt2121.camfound.model.CamFindToken;

import java.io.File;

import rx.Observable;

/**
 * Created by pt2121 on 8/19/15.
 *
 * CamFind Interface facilitating communication with CamFind API
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

    /**
     * Polling CamFind API every 12 seconds until the result is ready: status is 'completed'.
     *
     * @param result the CamFindResult observable
     * @return the CamFindResult observable with 'completed' status
     */
    Observable<CamFindResult> pollCamFindForStatus(final Observable<CamFindResult> result);

    /**
     * Polling CamFind API until the result is ready: status is 'completed'.
     *
     * @param second polling interval
     * @param result the CamFindResult observable
     * @return the CamFindResult observable with 'completed' status
     */
    Observable<CamFindResult> pollCamFindForStatus(int second, final Observable<CamFindResult> result);

    /**
     * Post pictureFile to CamFind API.
     *
     * @param pictureFile a picture file
     * @param service     CamFind service
     * @return the CamFindResult observable
     */
    Observable<CamFindResult> getCamFindResultObservable(File pictureFile, final CamFindService service);

}
