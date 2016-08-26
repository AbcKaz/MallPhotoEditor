package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.berik.mallappgoods.R;
import com.example.berik.mallappgoods.activity.ManageGoodsActivity;
import com.example.berik.mallappgoods.activity.ManagePhotosActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import entity.Goods;
import entity.Photo;

/**
 * Created by Berik on 18.07.2016.
 */
public class AddGoodsPhotosAdapter extends RecyclerView.Adapter {

    public  final static String SER_KEY = "GoodsSer";
    private final String TAG="ManagePhotos";

    private Context ctx;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Photo> photoList;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private Activity activity;
    private OnLoadMoreListener onLoadMoreListener;
    private String editMode="";

    public AddGoodsPhotosAdapter(List<Photo> rows, RecyclerView recyclerView, Activity act,String editMode) {

        photoList = rows;
        activity = act;
        this.editMode=editMode;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return photoList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.test_item, parent, false);

            vh = new GoodsViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GoodsViewHolder) {

            try {

                Photo photos = photoList.get(position);
                if(photos.getId()!=-1) {
                    Picasso.with(activity).load(photos.getDescription()).into(((GoodsViewHolder) holder).imageView);
                }else{
                    Picasso.with(activity).load("file://"+photos.getDescription()).into(((GoodsViewHolder) holder).imageView);
                }

                ((GoodsViewHolder)holder).tvPhotoId.setText(photos.getId());

            }catch (Exception ex){
                ex.printStackTrace();
            }

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView tvPhotoId;

        public GoodsViewHolder(View v) {
            super(v);

            imageView = (ImageView) v.findViewById(R.id.ivShow);
            tvPhotoId=(TextView) v.findViewById(R.id.photo_id);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getPosition();
                    Photo photo=photoList.get(position);

                    Log.d(TAG,"photo id="+ photo.getId());

                    Goods goods=new Goods();
                    goods.setPhotoList(photoList);

                    Intent intent = new Intent(activity, ManagePhotosActivity.class);
                    Bundle mBundle = new Bundle();

                    mBundle.putSerializable(Photo.SER_KEY, goods);
                    intent.putExtras(mBundle);
                    intent.putExtra("EditMode", editMode);

                    //activity.startActivity(intent);
                    activity.startActivityForResult(intent,300);

                }
            });


        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}