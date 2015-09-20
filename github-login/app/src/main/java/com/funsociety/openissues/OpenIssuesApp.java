package com.funsociety.openissues;

import com.alorma.github.basesdk.client.credentials.GithubDeveloperCredentials;
import com.alorma.github.basesdk.client.credentials.MetaDeveloperCredentialsProvider;

import android.app.Application;

/**
 * Created by pt2121 on 9/19/15.
 */
public class OpenIssuesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GithubDeveloperCredentials.init(new MetaDeveloperCredentialsProvider(this));
    }
}
