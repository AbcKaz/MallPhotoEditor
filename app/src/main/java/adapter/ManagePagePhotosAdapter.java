package adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.berik.mallappgoods.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import entity.Photo;

/**
 * Created by Berik on 20.08.2016.
 */
public class ManagePagePhotosAdapter  extends PagerAdapter {

    private List<Photo> photoList;
    private Context ctx;
    private LayoutInflater layoutInflater;

    public ManagePagePhotosAdapter(Context ctx,List<Photo> photoList ){
        this.ctx=ctx;
        this.photoList=photoList;
    }


    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view=layoutInflater.inflate(R.layout.manage_photos_item,container,false);

        ImageView imageView = (ImageView)item_view.findViewById(R.id.image_view);

        if(photoList.get(position).getId()!=-1) {
            Picasso.with(ctx).load(photoList.get(position).getDescription()).into(imageView);
        } else {
            Picasso.with(ctx).load("file://" + photoList.get(position).getDescription()).into(imageView);
        }

        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object );
    }
}
