package com.prt2121.recycler.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ptanapai on 4/29/2015.
 */
public class SnapLinearLayoutManager extends LinearLayoutManager implements ISnapLayoutManager {

    public SnapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        final LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    protected int getHorizontalSnapPreference() {
                        return SNAP_TO_START; // SNAP_TO_ANY
                    }

                    protected int getVerticalSnapPreference() {
                        return SNAP_TO_START; // SNAP_TO_ANY
                    }

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return SnapLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public int getFixScrollPos() {
        if (this.getChildCount() == 0) {
            return 0;
        }

        final View child = getChildAt(0);
        final int childPos = getPosition(child);

        if (getOrientation() == HORIZONTAL
                && Math.abs(child.getLeft()) > child.getMeasuredWidth() / 2) {
            // Scrolled first view more than halfway offscreen
            return childPos + 1;
        }
        return childPos;
    }

}
