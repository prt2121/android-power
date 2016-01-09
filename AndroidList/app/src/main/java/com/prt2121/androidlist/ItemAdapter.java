package com.prt2121.androidlist;

import android.content.Context;
import android.os.Looper;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

/**
 * Created by pt2121 on 1/9/16.
 */
public class ItemAdapter extends BaseAdapter {

  private List<Item> items = Collections.emptyList();
  private final Context context;

  public ItemAdapter(Context context) {
    this.context = context;
  }

  public void update(List<Item> items) {
    checkOnMainThread();
    this.items = items;
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return items.size();
  }

  @Override public Item getItem(int position) {
    return items.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);
    }
    TextView title = ViewHolder.get(convertView, R.id.titleTextView);
    TextView description = ViewHolder.get(convertView, R.id.descTextView);
    Item item = getItem(position);
    title.setText(item.getTitle());
    description.setText(item.getDescription());
    return convertView;
  }

  private void checkOnMainThread() {
    if (BuildConfig.DEBUG) {
      if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
        throw new IllegalStateException("This method should be called from the Main Thread");
      }
    }
  }

  public static class ViewHolder {
    @SuppressWarnings("unchecked") public static <T extends View> T get(View view, int id) {
      SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
      if (viewHolder == null) {
        viewHolder = new SparseArray<>();
        view.setTag(viewHolder);
      }
      View childView = viewHolder.get(id);
      if (childView == null) {
        childView = view.findViewById(id);
        viewHolder.put(id, childView);
      }
      return (T) childView;
    }
  }
}
