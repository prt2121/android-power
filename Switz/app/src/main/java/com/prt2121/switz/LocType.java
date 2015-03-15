package com.prt2121.switz;

/**
 * Created by pt2121 on 3/13/15.
 */
final public class LocType {

    public final String name;

    private boolean checked;

    public LocType(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
