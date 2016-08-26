package com.example.berik.mallappgoods.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.berik.mallappgoods.R;

import java.util.List;

import adapter.ManageGoodsAdapter;
import entity.Goods;
import rest.GoodsApi;
import rest.Helper;
import rest.ServiceGenerator;

public class ManageGoodsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Goods> goodsList;
    private ManageGoodsAdapter mAdapter;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_goods);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.manageGoodsList);

        if (toolbar != null) {
            toolbar.setTitle("Тауарлар");
            setSupportActionBar(toolbar);

            pDialog = new ProgressDialog(this);
            pDialog.setCancelable(false);
        }

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        pDialog.setMessage("загружается ...");
        showDialog();
        mAdapter = new ManageGoodsAdapter(goodsList, mRecyclerView, this);
        new LoadGoods().execute(String.valueOf(14));

    }

    class LoadGoods extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            GoodsApi client = ServiceGenerator.createService(GoodsApi.class, Helper.API_URL2);
            goodsList = client.getGoodsByShop("fieldByShopId","14");
            mAdapter = new ManageGoodsAdapter(goodsList, mRecyclerView, ManageGoodsActivity.this);
            return null;
        }

        protected void onPostExecute(String unused) {
            hideDialog();
            mRecyclerView.setAdapter(mAdapter);
            if (goodsList.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_goods, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_add_goods) {
            Intent intent = new Intent(getApplicationContext(), AddGoodsActivity.class);
            intent.putExtra("EditMode", "add");
            intent.putExtra("ShopId",14);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}




















