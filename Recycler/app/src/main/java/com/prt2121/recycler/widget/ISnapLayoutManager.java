package com.prt2121.recycler.widget;

/**
 * Created by ptanapai on 4/29/2015.
 */

/**
 * An interface that LayoutManagers that should snap to grid should implement.
 */
public interface ISnapLayoutManager {

    /**
     * @return the position this list must scroll to to fix a state where the
     * views are not snapped to grid.
     */
    int getFixScrollPos();

}
