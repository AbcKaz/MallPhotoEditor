package com.example.berik.mallappgoods.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.berik.mallappgoods.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import adapter.AddGoodsPhotosAdapter;
import adapter.ManageGoodsAdapter;
import adapter.ManagePagePhotosAdapter;
import adapter.PhotosAdapter;
import entity.CustomPhotos;
import entity.Goods;
import entity.Message;
import entity.Photo;
import rest.GoodsApi;
import rest.Helper;
import rest.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddGoodsActivity extends AppCompatActivity {

    public final String TAG="AddGoods";
    private  Toolbar toolbar;
    ViewSwitcher viewSwitcher;

    ImageLoader imageLoader;

    GridView gridGallery;
    Handler handler;
    PhotosAdapter adapter;
    private Button btnAddGoods;

    private ImageView imgAdd;

    private TextInputLayout goodsNameLayout;
    private TextInputLayout goodsDesciptionLayout;
    private EditText goodsName;
    private EditText goodsDescription;
    private EditText goodsPrice;

    String[] all_path;
    private ProgressDialog pDialog;
    private String editMode;
    private Goods goods;
    private String shopId;

    //---
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Photo> photoList;
    private AddGoodsPhotosAdapter mAdapter;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        photoList=new ArrayList<>();

        //----
        mRecyclerView = (RecyclerView) findViewById(R.id.addGoodsPhotosRv);
        linearLayout=(LinearLayout)findViewById(R.id.llRview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AddGoodsPhotosAdapter(photoList, mRecyclerView, this,editMode);

        goodsNameLayout = (TextInputLayout)findViewById(R.id.goods_name_layout);
        goodsDesciptionLayout = (TextInputLayout)findViewById(R.id.goods_description_layout);

        goodsName=(EditText)findViewById(R.id.goods_name);
        goodsDescription=(EditText)findViewById(R.id.goods_description);
        goodsPrice=(EditText)findViewById(R.id.goods_price);

        Intent intent=getIntent();
        editMode=intent.getStringExtra("EditMode");
        if(editMode.equals("edit")){

            goods= (Goods) getIntent().getSerializableExtra(Goods.SER_KEY);
            fillEditGoodsFn(true);
            Log.d(TAG,"manage goods:edit");

            toolbar.setTitle("Изменение товара");
            setSupportActionBar(toolbar);

        }else {

            shopId=intent.getStringExtra("ShopId");
            Log.d(TAG,"manage goods:add");
            fillEditGoodsFn(false);

            toolbar.setTitle("Добавление товара");
            setSupportActionBar(toolbar);
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        initImageLoader();

        /*gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new PhotosAdapter(getApplicationContext(), imageLoader);
        adapter.setMultiplePick(false);
        gridGallery.setAdapter(adapter);
        */

        //-------------добавить фото
        imgAdd=(ImageView)findViewById(R.id.imgAdd);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Helper.ACTION_MULTIPLE_PICK);
                if(editMode.equals("edit")) {

                    int img_count=goods.getPhotoList().size();

                    if(img_count<4) {
                        startActivityForResult(i, 200);
                    }else{
                        Toast.makeText(getApplicationContext(), "Вы можете выбрать максимум 4 изображений!!!",Toast.LENGTH_SHORT).show();
                    }

                }else{

                    startActivityForResult(i, 200);
                }

            }
        });

        //--------------сохранить запись
        btnAddGoods=(Button)findViewById(R.id.btnAddGoods);
        btnAddGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editMode.equals("add")) {
                    Log.d(TAG,"manage goods2:add");
                   addGoodsFn();
                }
                else {
                    Log.d(TAG,"manage goods2:edit");
                    editGoodsFn();
                }

            }
        });

    }

    //----добавить
    private void addGoodsFn(){

        pDialog.setMessage("загружается ...");
        showDialog();

        GoodsApi api = ServiceGenerator.createService(GoodsApi.class, Helper.API_URL2);
        HashMap<String,String> arr=new HashMap<String, String>();

        String name = goodsName.getText().toString();

        if (validateData(name)) {

            String description=(goodsDescription.getText()!=null)?goodsDescription.getText().toString():"null";
            String price=(goodsPrice.getText()!=null)?goodsPrice.getText().toString():"null";

            arr.put("shop_id",shopId);
            arr.put("name",name);
            arr.put("description",description);
            arr.put("price",price);

            try {

                if(all_path.length>0) {
                    arr.put("img_count",String.valueOf(all_path.length));
                    int indx=0;
                    for (String item : all_path) {
                        String encodedImage = getBase64String(item);
                        arr.put("image" + indx, encodedImage);
                        indx++;
                    }
                }

            }catch (Exception ex){
                Log.d(TAG,"error image put:"+ex.getMessage());
            }

            api.createGoods(arr, new Callback<Message>() {
                @Override
                public void success(Message message, Response response) {
                    hideDialog();
                    if (!message.isError()) {

                        Toast.makeText(getApplicationContext(), "Успешно добавлено!", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(getBaseContext(),ManageGoodsActivity.class);
                        startActivity(intent);

                    }else {
                        Toast.makeText(getApplicationContext(), "Ошибка: " + message.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG,"server error:"+message.getMessage());
                }

                @Override
                public void failure(RetrofitError error) {
                    hideDialog();
                    Toast.makeText(getApplicationContext(), "Ошибка: "+error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG,"server failure:"+error.getMessage());
                }
            });
        }

    }

    //----изменить
    private void fillEditGoodsFn(boolean isEdit){

        if(isEdit) {

            if (goods != null) {

                Log.d(TAG," edit goods :"+goods.getName());

                goodsName.setText(goods.getName());
                goodsDescription.setText(goods.getDescription());
                goodsPrice.setText(String.valueOf(goods.getPrice()));

                photoList=goods.getPhotoList();

                if(photoList.size()!=0) {

                    Log.d("ImageView","null");
                    mAdapter = new AddGoodsPhotosAdapter(photoList, mRecyclerView, this,editMode);
                    mRecyclerView.setAdapter(mAdapter);

                }else{
                    Log.d("ImageView","null");
                }

            }

        } else {
            goodsName.setText(null);
            goodsDescription.setText(null);
            goodsPrice.setText(null);
        }
    }

    private void editGoodsFn(){

        pDialog.setMessage("загружается ...");
        showDialog();

        GoodsApi api = ServiceGenerator.createService(GoodsApi.class, Helper.API_URL2);
        HashMap<String,String> arr=new HashMap<String, String>();

        String name = goodsName.getText().toString();

        if (validateData(name)) {

            String description=(goodsDescription.getText()!=null)?goodsDescription.getText().toString():"null";
            String price=(goodsPrice.getText()!=null)?goodsPrice.getText().toString():"null";

            arr.put("goods_id",String.valueOf(goods.getId()));
            arr.put("name",name);
            arr.put("description",description);
            arr.put("price",price);

            try {

                if(all_path.length>0) {

                    int indx=0;
                    for (String item : all_path) {
                        String encodedImage = getBase64String(item);

                        if(encodedImage!=null) {
                            arr.put("image" + indx, encodedImage);
                            indx++;
                        }
                    }

                    arr.put("img_count",String.valueOf(indx));
                }

            }catch (Exception ex){
                Log.d(TAG,"error image put:"+ex.getMessage());
            }

            api.updateGoods(arr, new Callback<Message>() {
                @Override
                public void success(Message message, Response response) {

                    hideDialog();
                    if (!message.isError()) {

                        Toast.makeText(getApplicationContext(), "Успешно изменено!", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(getBaseContext(),ManageGoodsActivity.class);
                        startActivity(intent);

                    }else {
                        Toast.makeText(getApplicationContext(), "Ошибка: " + message.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG,"server error:"+message.getMessage());
                }

                @Override
                public void failure(RetrofitError error) {
                    hideDialog();
                    Toast.makeText(getApplicationContext(), "Ошибка: "+error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG,"server failure:"+error.getMessage());
                }
            });
        }

    }

    public static String getBase64String(String filepath) {
        try {
            File imagefile = new File(filepath);

            long  c=imagefile.length()/1024;
            if(c>1000) {
                Log.d("AddGoods", "max file size=1024, your file=" + imagefile.length());
                return null;
            }

            FileInputStream fis= new FileInputStream(imagefile);

            Bitmap bm = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            return encodedImage;
        } catch (FileNotFoundException e) {
            Log.d("AddGoods","file to convert base64 error:"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private boolean validateData(String name) {
        boolean result = true;
        if (Helper.isEmpty(name)) {
            // We set the error message
            goodsNameLayout.setError(getString(R.string.error_empty_title));
            result = false;
        } else // We remove error messages
            goodsNameLayout.setErrorEnabled(false);
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                all_path = data.getStringArrayExtra("all_path");

                for (String string : all_path) {
                    Photo photo = new Photo();
                    photo.setDescription(string);
                    photo.setId(-1);
                    photoList.add(photo);
                }

                mAdapter = new AddGoodsPhotosAdapter(photoList, mRecyclerView, this, editMode);
                mRecyclerView.setAdapter(mAdapter);
                //adapter.addAll(dataT);
            }
        } else if (requestCode == 300) {

            if (resultCode == Activity.RESULT_OK) {

                goods = (Goods) data.getSerializableExtra(Photo.SER_KEY);
                mAdapter = new AddGoodsPhotosAdapter(goods.getPhotoList(), mRecyclerView, this, editMode);
                mRecyclerView.setAdapter(mAdapter);

            }

        }
    }

    private void dd() {

        Goods goods = new Goods();
        goods.setPhotoList(photoList);

        Intent intent = new Intent(Helper.ACTION_MULTIPLE_PICK2);
        Bundle mBundle = new Bundle();

        mBundle.putSerializable(Photo.SER_KEY, goods);
        intent.putExtras(mBundle);
        intent.putExtra("EditMode", editMode);

        //activity.startActivity(intent);
        startActivityForResult(intent, 300);

    }

    //---диалоговый окно
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
