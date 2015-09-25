package com.prt2121.githubsdk.service.auth;

import com.prt2121.githubsdk.model.request.RequestTokenDTO;
import com.prt2121.githubsdk.model.response.Token;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by pt2121 on 9/24/15.
 */
public interface LoginService {

  @POST("/login/oauth/access_token") Observable<Token> token(@Body RequestTokenDTO requestTokenDTO);
}
