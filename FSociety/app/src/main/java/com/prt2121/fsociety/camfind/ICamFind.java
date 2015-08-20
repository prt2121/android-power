package com.prt2121.fsociety.camfind;

import com.prt2121.fsociety.camfind.model.CamFindResult;
import com.prt2121.fsociety.camfind.model.CamFindToken;

import rx.Observable;

/**
 * Created by pt2121 on 8/19/15.
 */
public interface ICamFind {

    Observable<CamFindToken> postImage(CamFindService service, String path);

    Observable<CamFindResult> getCamFindImageResponse(CamFindService service, String token);

}
