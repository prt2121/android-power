package com.prt2121.bees;

import com.prt2121.bees.userlocation.UserLocationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by pt2121 on 9/9/15.
 */
@Singleton
@Component(modules = {
        UserLocationModule.class
})
public interface Graph {

    void inject(MapsActivity fragment);

}
