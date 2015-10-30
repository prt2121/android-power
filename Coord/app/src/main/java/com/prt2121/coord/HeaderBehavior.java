package com.prt2121.coord;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by pt2121 on 10/20/15.
 */
@SuppressWarnings("unused") public class HeaderBehavior
    extends CoordinatorLayout.Behavior<HeaderView> {

  private Context context;

  private int startMarginLeft;
  private int endMarginLeft;
  private int marginRight;
  private int startMarginBottom;
  private boolean isHide;
  //private float titleTextSize = -1;
  //private float subtitleTextSize = -1;
  int startWidth = -1;
  int startHeight = -1;

  public HeaderBehavior(Context context, AttributeSet attrs) {
    this.context = context;
  }

  @Override
  public boolean layoutDependsOn(CoordinatorLayout parent, HeaderView child, View dependency) {
    return dependency instanceof AppBarLayout;
  }

  @Override public boolean onDependentViewChanged(CoordinatorLayout parent, HeaderView child,
      View dependency) {

    int maxScroll = ((AppBarLayout) dependency).getTotalScrollRange();
    float percentage = Math.abs(dependency.getY()) / (float) maxScroll;

    float childPosition = dependency.getHeight() + dependency.getY() - child.getHeight()
        - (getToolbarHeight() - child.getHeight()) * percentage / 2;

    childPosition = childPosition - startMarginBottom * (1f - percentage);

    //if (titleTextSize == -1 || subtitleTextSize == -1) {
    //  titleTextSize = child.title.getTextSize();
    //  subtitleTextSize = child.subTitle.getTextSize();
    //}
    //child.title.setTextSize(titleTextSize * (1 - percentage));
    //child.subTitle.setTextSize(subtitleTextSize * (1 - percentage));

    CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();

    if(startWidth == -1 || startHeight == -1) {
      startWidth = lp.width;
      startHeight = lp.height;
    }
    lp.width = (int) ((1 - percentage) * startWidth);
    lp.height = (int) ((1 - percentage) * startHeight);
    lp.leftMargin = (int) (percentage * endMarginLeft) + startMarginLeft;
    lp.rightMargin = marginRight;
    child.setLayoutParams(lp);

    child.setY(childPosition);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      if (isHide && percentage < 1) {
        child.setVisibility(View.VISIBLE);
        isHide = false;
      } else if (!isHide && percentage == 1) {
        child.setVisibility(View.GONE);
        isHide = true;
      }
    }
    return true;
  }

  public int getToolbarHeight() {
    int result = 0;
    TypedValue tv = new TypedValue();
    if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      result = TypedValue.complexToDimensionPixelSize(tv.data,
          context.getResources().getDisplayMetrics());
    }
    return result;
  }
}
