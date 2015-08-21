package com.prt2121.camfound.model;

import com.google.gson.annotations.Expose;

public class CamFindToken {

    @Expose
    private String token;

    @Expose
    private String url;

    /**
     * @return The token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
