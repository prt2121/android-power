package com.prt2121.coord;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by pt2121 on 10/20/15.
 */
@SuppressWarnings("unused") public class HeaderBehavior
    extends CoordinatorLayout.Behavior<HeaderView> {

  private final static float MIN_PERCENTAGE_SIZE = 0.3f;
  private final Context context;
  private float maxSize;
  private float padding;

  private int startXPosition;
  private int finalXPosition;
  private int startYPosition;
  private int finalYPosition;
  private int finalHeight;
  private int startHeight;
  private float startToolbarPosition;

  public HeaderBehavior(Context context, AttributeSet attrs) {
    this.context = context;
    maxSize = context.getResources().getDimension(R.dimen.max_width);
    padding = 0;
  }

  @Override
  public boolean layoutDependsOn(CoordinatorLayout parent, HeaderView child, View dependency) {
    return dependency instanceof Toolbar;
  }

  @Override public boolean onDependentViewChanged(CoordinatorLayout parent, HeaderView child,
      View dependency) {

    // Called once
    if (startYPosition == 0) {
      startYPosition = (int) (child.getY() + (child.getHeight() / 2));
    }
    if (finalYPosition == 0) {
      finalYPosition = (dependency.getHeight() / 2);
    }
    if (startHeight == 0) {
      startHeight = child.getHeight();
    }
    if (finalHeight == 0) {
      finalHeight = context.getResources().getDimensionPixelOffset(R.dimen.final_width);
    }

    if (startXPosition == 0) {
      startXPosition = (int) (child.getX() + (child.getWidth() / 2));
    }

    if (finalXPosition == 0) {
      finalXPosition = context.getResources()
          .getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + (finalHeight
          / 2);
    }

    if (startToolbarPosition == 0) {
      startToolbarPosition = dependency.getY() + (dependency.getHeight() / 2);
    }

    final int maxScrollDistance = (int) (startToolbarPosition - getStatusBarHeight());
    float expandedPercentage = dependency.getY() / maxScrollDistance;

    float distanceYToSubtract =
        ((startYPosition - finalYPosition) * (1f - expandedPercentage)) + (child.getHeight() / 2);

    float distanceXToSubtract =
        ((startXPosition - finalXPosition) * (1f - expandedPercentage)) + (child.getWidth() / 2);

    float heightToSubtract = (startHeight - finalHeight) * (1f - expandedPercentage);

    child.setY(startYPosition - distanceYToSubtract);
    child.setX(startXPosition - distanceXToSubtract);

    int proportionalAvatarSize = (int) (maxSize * (expandedPercentage));

    CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
    lp.width = (int) (startHeight - heightToSubtract);
    lp.height = (int) (startHeight - heightToSubtract);
    child.setLayoutParams(lp);
    return true;
  }

  public int getStatusBarHeight() {
    int result = 0;
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = context.getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }
}
