package com.prt2121.switz;

import java.util.List;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by pt2121 on 3/13/15.
 */
@Singleton
@Component(modules = LocTypeModule.class)
public interface Graph {

    List<LocType> locTypes();

    void inject(LocTypeFragment fragment);

}
