package com.prt2121.androidlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private ListView listView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listView = (ListView) findViewById(R.id.listView);
    final ItemAdapter adapter = new ItemAdapter(this);
    adapter.update(initItems());
    listView.setAdapter(adapter);
  }

  private List<Item> initItems() {
    Item item1 = new Item("Title 1", "Desc 1");
    Item item2 = new Item("Title 2", "Desc 2");
    Item item3 = new Item("Title 3", "Desc 3");
    Item item4 = new Item("Title 4", "Desc 4");
    Item item5 = new Item("Title 5", "Desc 5");
    List<Item> items = new ArrayList<>(5);
    items.add(item1);
    items.add(item2);
    items.add(item3);
    items.add(item4);
    items.add(item5);
    return items;
  }
}
