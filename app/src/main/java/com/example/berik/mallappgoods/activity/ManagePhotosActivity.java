package com.example.berik.mallappgoods.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.berik.mallappgoods.R;

import java.util.List;

import adapter.ManagePagePhotosAdapter;
import entity.Goods;
import entity.Message;
import entity.Photo;
import rest.GoodsApi;
import rest.Helper;
import rest.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ManagePhotosActivity extends AppCompatActivity {

    public final String TAG="managePhoto";
    private ViewPager viewPager;
    private ManagePagePhotosAdapter adapter;
    private List<Photo> photoList;
    private Button btnDel,btnRotate;

    String editMode="";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_photos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        editMode=intent.getStringExtra("EditMode");

        Goods goods;
        goods= (Goods) getIntent().getSerializableExtra(Photo.SER_KEY);
        photoList=goods.getPhotoList();

        viewPager=(ViewPager)findViewById(R.id.view_pager);
        adapter=new ManagePagePhotosAdapter(this,photoList);
        viewPager.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        final AlertDialog.Builder alert= new AlertDialog.Builder(this)
                .setTitle("Удалить")
                .setMessage("Вы уверены, что хотите удалить эту запись?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        final int position = viewPager.getCurrentItem();
                        Photo photo=photoList.get(position);

                        if(photo.getId()!=-1) {

                            GoodsApi api = ServiceGenerator.createService(GoodsApi.class, Helper.API_URL2);
                            api.deletePhotos(String.valueOf(photo.getId()), new Callback<Message>() {
                                @Override
                                public void success(Message message, Response response) {
                                    hideDialog();
                                    if (!message.isError()) {

                                        Toast.makeText(getApplicationContext(), "Успешно удалено!", Toast.LENGTH_LONG).show();
                                        photoList.remove(position);

                                        adapter = new ManagePagePhotosAdapter(getApplicationContext(), photoList);
                                        viewPager.setAdapter(adapter);

                                        //  Intent intent=new Intent(getBaseContext(),ManageGoodsActivity.class);
                                        //  startActivity(intent);

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Ошибка: " + message.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    Log.d(TAG, "server error:" + message.getMessage());
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    hideDialog();
                                    Toast.makeText(getApplicationContext(), "Ошибка: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "server failure:" + error.getMessage());
                                }
                            });

                        }else{

                            photoList.remove(position);

                            adapter = new ManagePagePhotosAdapter(getApplicationContext(), photoList);
                            viewPager.setAdapter(adapter);


                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        //---удалить
        btnDel=(Button)findViewById(R.id.btnDel);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });

        //---повернуть
        btnRotate=(Button)findViewById(R.id.btnRotate);
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();

                Bundle mBundle = new Bundle();
                Goods goods=new Goods();
                goods.setPhotoList(photoList);

                //mBundle.putSerializable(Photo.SER_KEY, goods);

                //sendIntent.putExtra("EditMode", editMode);
                //sendIntent.putExtra("fromInten", "ManagePhotosAdapter");
                mBundle.putString("name","Berik");

                sendIntent.putExtras(mBundle);

                setResult(Activity.RESULT_OK, sendIntent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent sendIntent = new Intent();
        Bundle mBundle = new Bundle();
        Goods goods=new Goods();
        goods.setPhotoList(photoList);

        mBundle.putSerializable(Photo.SER_KEY, goods);
        sendIntent.putExtra("EditMode", editMode);
        sendIntent.putExtra("fromInten", "ManagePhotosAdapter");

        sendIntent.putExtras(mBundle);
        setResult(Activity.RESULT_OK, sendIntent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
/*
        Intent sendIntent = new Intent();
        Bundle mBundle = new Bundle();
        Goods goods=new Goods();
        goods.setPhotoList(photoList);

        //mBundle.putSerializable(Photo.SER_KEY, goods);

        //sendIntent.putExtra("EditMode", editMode);
        //sendIntent.putExtra("fromInten", "ManagePhotosAdapter");
        mBundle.putString("name","Berik");

        sendIntent.putExtras(mBundle);

        setResult(Activity.RESULT_OK, sendIntent);
        finish();*/
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
