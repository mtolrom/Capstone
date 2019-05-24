package org.mekonecampus.mekonecapstone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private static Context mContext;
    private static List<Article> mData;
    static  String category;
    static String website;
    Activity acti;
    static  ImageView myImg;


    public RecyclerViewAdapter(Context mContext, List<Article> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_book, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            holder.tv_book_title.setText(mData.get(position).Title);
            holder.tv_book_author.setText(mData.get(position).Body);
            Glide.with(mContext).load(mData.get(position).PicUrl).fitCenter().into(holder.img_book_thumbnail);

            holder.img_book_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://" + mData.get(position).Custom4;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    mContext.startActivity(i);
                }
            });
        }catch (Exception ex){
            String str = ex.toString();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_book_title;
        TextView tv_book_author;
        ImageView img_book_thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_book_title = (TextView) itemView.findViewById(R.id.book_title_id);
            tv_book_author = (TextView) itemView.findViewById(R.id.book_author_id);
            img_book_thumbnail = (ImageView) itemView.findViewById(R.id.book_img_id);
        }
    }
}


