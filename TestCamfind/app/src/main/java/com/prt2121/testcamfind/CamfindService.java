package com.prt2121.testcamfind;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;

/**
 * Created by pt2121 on 8/16/15.
 */
public interface CamfindService {

    String CAMFIND_URL = "https://api.cloudsightapi.com";

    String DEFAULT_LOCALE = "en-US";

    String CAMFIND_KEY = "";

    // https://cloudsight.readme.io/docs/testinput
    // image_request[image]
    // image_request[locale]
    @Multipart
    @POST("/image_requests")
    Observable<Response> imageRequest(
            @Header("Authorization") String key,
            @Part("image_request[image]") TypedFile file,
            @Part("image_request[locale]") TypedString locale);

    // https://cloudsight.readme.io/docs/image_responses
    @GET("/image_responses/{token}")
    Observable<Response> imageResponse(@Header("Authorization") String key,
            @Path("token") String token);
}
