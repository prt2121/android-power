
package com.prt2121.fsociety.camfind.model;

import com.google.gson.annotations.Expose;

public class CamFindResult {

    @Expose
    private String status;

    @Expose
    private String name;

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

}
