package com.prt2121.github.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Progress dialog in Holo Light theme
 */
public class LightProgressDialog extends ProgressDialog {

  private LightProgressDialog(Context context, CharSequence message) {
    super(context, THEME_HOLO_LIGHT);
  }

  /**
   * Create progress dialog
   *
   * @return dialog
   */
  public static AlertDialog create(Context context, int resId) {
    return create(context, context.getResources().getString(resId));
  }

  /**
   * Create progress dialog
   *
   * @return dialog
   */
  public static AlertDialog create(Context context, CharSequence message) {
    ProgressDialog dialog = new LightProgressDialog(context, message);
    dialog.setMessage(message);
    dialog.setIndeterminate(true);
    dialog.setProgressStyle(STYLE_SPINNER);
    return dialog;
  }
}
