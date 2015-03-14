package com.prt2121.switz;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pt2121 on 3/13/15.
 */
// TODO make it a singleton module
public class LocTypeModule {

    private static LocTypeModule instance;

//    private int type = 2;
//
//    private int number = 4;

    private List<LocType> types = new ArrayList<>();

    //LocType
    private LocTypeModule() {
        types.add(new LocType("User", true));
        types.add(new LocType("Bin", true));
        types.add(new LocType("Supermarket/Grocery", true));
        types.add(new LocType("Drop-Off", true));
    }

    public static LocTypeModule getInstance() {
        if (instance == null) {
            instance = new LocTypeModule();
        }
        return instance;
    }

    public List<LocType> getTypes() {
        return types;
    }

    public void setTypes(List<LocType> types) {
        this.types = types;
    }

}
