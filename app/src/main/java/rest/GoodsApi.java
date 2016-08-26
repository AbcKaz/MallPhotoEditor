package rest;

import android.app.Notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import entity.Goods;
import entity.Message;
import entity.Photo;
import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

/**
 * Created by Berik on 15.07.2016.
 */
public interface GoodsApi {

    @GET("/findGoodsByField")
    List<Goods> getGoodsByShop(
            @Query("method") String method,
            @Query("val") String val);

    @Multipart
    @POST("/insertGoods")
    void create(@Part("shop_id") String shop_id, @Part("name") String name, @Part("price") String price, @Part("description") String description, Callback<Message> message);

    @Multipart
    @POST("/upload4.php")
    void create3(@Part("name") String name, Callback<Message> message);

    @FormUrlEncoded
    @POST("/upload4.php")
    void create4(@FieldMap HashMap<String,String> parama , Callback<Message> message);

    @FormUrlEncoded
    @POST("/insertGoods")
    void createGoods(@FieldMap HashMap<String,String> parama , Callback<Message> message);

    @FormUrlEncoded
    @POST("/updateGoods")
    void updateGoods(@FieldMap HashMap<String,String> parama , Callback<Message> message);

    @Multipart
    @POST("/deletePhotos")
    void deletePhotos(@Part("photo_id") String photo_id, Callback<Message> message);

    @Multipart
    @GET("/getPhotos")
    List<Photo> getPhotos(
            @Query("goods_id") String goods_id);


}
