package com.example.berik.mallappgoods.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.berik.mallappgoods.R;

public class GoodsListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Goods Manager");
        setSupportActionBar(toolbar);
    }
}
