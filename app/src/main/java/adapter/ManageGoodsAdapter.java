package adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.berik.mallappgoods.R;
import com.example.berik.mallappgoods.activity.AddGoodsActivity;

import java.util.List;

import entity.Goods;

/**
 * Created by Berik on 15.07.2016.
 */
public class ManageGoodsAdapter  extends RecyclerView.Adapter {

    public  final static String SER_KEY = "GoodsSer";

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Goods> goodsList;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private Activity activity;
    private OnLoadMoreListener onLoadMoreListener;

    public ManageGoodsAdapter(List<Goods> rows, RecyclerView recyclerView, Activity act) {
        goodsList = rows;
        activity = act;
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
        return goodsList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.manage_goods_item, parent, false);

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

            Goods goods = (Goods) goodsList.get(position);

            ((GoodsViewHolder) holder).name.setText(goods.getName());
            ((GoodsViewHolder) holder).goods_view.setText("Просмотры: "+goods.getView());
            ((GoodsViewHolder) holder).goodsId.setText(String.valueOf(goods.getId()));
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView goods_view;
        public TextView goodsId;
        public TextView editGoodsView;

        public GoodsViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.goods_name);
            goods_view = (TextView) v.findViewById(R.id.goods_view);
            goodsId = (TextView) v.findViewById(R.id.goods_id);
            editGoodsView = (TextView)v.findViewById(R.id.editGoodsView);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  /*  Log.d("Shop id = ", shopId.getText().toString());
                    Intent intent = new Intent(activity, ManageGoodsActivity.class);
                    intent.putExtra("shopId", shopId.getText().toString());
                    intent.putExtra("shopName",name.toString());
                    activity.startActivity(intent);*/
                }
            });

            editGoodsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getPosition();
                    SerializeMethod(position);

                }
            });

        }

        void SerializeMethod(int position){

            //goodId
            Intent intent = new Intent(activity, AddGoodsActivity.class);
            Bundle mBundle = new Bundle();
            Goods goods=goodsList.get(position);

            intent.putExtra("fromInten", "ManageGoodsAdapter");
            intent.putExtra("EditMode", "edit");

            mBundle.putSerializable(Goods.SER_KEY, goods);
            intent.putExtras(mBundle);
            activity.startActivity(intent);
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