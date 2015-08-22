package com.prt2121.camfound;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * an animation for resizing the view.
 *
 * Created by pt2121 on 8/22/15.
 */
public class ResizeAnimation extends Animation {

    private View mView;

    private float mToHeight;

    private float mFromHeight;

    private float mToWidth;

    private float mFromWidth;

    public ResizeAnimation(View v, float fromWidth, float fromHeight, float toWidth, float toHeight) {
        mToHeight = toHeight;
        mToWidth = toWidth;
        mFromHeight = fromHeight;
        mFromWidth = fromWidth;
        mView = v;
        setDuration(500);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height =
                (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
        ViewGroup.LayoutParams p = mView.getLayoutParams();
        p.height = (int) height;
        p.width = (int) width;
        mView.requestLayout();
    }

    public static class Builder {

        private View view;

        private float fromWidth;

        private float fromHeight;

        private float toWidth;

        private float toHeight;

        public Builder setView(View v) {
            view = v;
            return this;
        }

        public Builder setFromWidth(float fromWidth) {
            this.fromWidth = fromWidth;
            return this;
        }

        public Builder setFromHeight(float fromHeight) {
            this.fromHeight = fromHeight;
            return this;
        }

        public Builder setToWidth(float toWidth) {
            this.toWidth = toWidth;
            return this;
        }

        public Builder setToHeight(float toHeight) {
            this.toHeight = toHeight;
            return this;
        }

        public ResizeAnimation createResizeAnimation() {
            return new ResizeAnimation(view, fromWidth, fromHeight, toWidth, toHeight);
        }
    }
}
