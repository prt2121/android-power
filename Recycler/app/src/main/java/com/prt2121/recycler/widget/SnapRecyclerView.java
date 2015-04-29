package com.prt2121.recycler.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ptanapai on 4/29/2015.
 */
public final class SnapRecyclerView extends RecyclerView {

    public SnapRecyclerView(Context context) {
        super(context);
    }

    public SnapRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final boolean ret = super.onTouchEvent(e);
        final LayoutManager lm = getLayoutManager();
        if (lm instanceof ISnapLayoutManager
                && (e.getAction() == MotionEvent.ACTION_UP ||
                e.getAction() == MotionEvent.ACTION_CANCEL)
                && getScrollState() == SCROLL_STATE_IDLE) {
            smoothScrollToPosition(((ISnapLayoutManager) lm).getFixScrollPos());
        }
        return ret;
    }
}