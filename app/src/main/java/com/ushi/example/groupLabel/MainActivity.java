package com.ushi.example.groupLabel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.ushi.example.groupLabel.adapter.ExampleAdapter;
import com.ushi.example.groupLabel.widget.GroupingItemDecoration;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.addItemDecoration(new GroupingItemDecoration(this));
        recycler.setAdapter(new ExampleAdapter());
    }
}
