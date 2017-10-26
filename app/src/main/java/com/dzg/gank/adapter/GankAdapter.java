package com.dzg.gank.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzg.gank.R;
import com.dzg.gank.module.GankBean;
import com.dzg.gank.ui.webview.WebViewActivity;
import com.dzg.gank.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dengzhouguang on 2017/10/13.
 */

public class GankAdapter extends RecyclerView.Adapter {
    public static int TYPE_IMAGE=1;
    public static int TYPE_NO_IMAGE=2;
    Context mContext;
    List<GankBean.ResultsBean> mList;
    public GankAdapter(Context context) {
        this.mContext=context;
        this.mList=new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getImages()!=null?TYPE_IMAGE:TYPE_NO_IMAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType==TYPE_IMAGE){
            View view = inflater.inflate(R.layout.recycler_item, parent, false);
            return new GankViewHolder(view);
        }else {
            View view = inflater.inflate(R.layout.recycler_item_noimage, parent, false);
            return new GankViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GankViewHolder){
            GankViewHolder myHolder = (GankViewHolder) holder;
            myHolder.bindData(mList.get(position));
        }else {
            GankViewHolder2 myHolder = (GankViewHolder2) holder;
            myHolder.bindData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void add(List<GankBean.ResultsBean> list){
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class GankViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_title)
        TextView titleTv;
        @BindView(R.id.item_img)
        ImageView imgIv;
        @BindView(R.id.item_time)
        TextView timeTv;
        @BindView(R.id.item_author)
        TextView authorTv;
        private String url;
        public GankViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindData(GankBean.ResultsBean entity) {
            titleTv.setText(entity.getDesc());
            timeTv.setText(entity.getPublishedAt().replace("T"," ").substring(0,entity.getPublishedAt().indexOf(".")!=-1?entity.getPublishedAt().indexOf("."):entity.getPublishedAt().length()));
            authorTv.setText(entity.getWho());
            url=entity.getUrl();
            if (entity.getImages()!=null){
                final ViewTreeObserver vto = titleTv.getViewTreeObserver();
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        titleTv.getViewTreeObserver().removeOnPreDrawListener(this);
                        int height = titleTv.getMeasuredHeight();
                        int width = titleTv.getMeasuredWidth();
                        imgIv.setLayoutParams(new LinearLayout.LayoutParams((int) CommonUtils.getDimens(R.dimen.nav_bar_height),height));
                        return true;
                    }
                });
                Glide.with(mContext).load(entity.getImages().get(0)).into(imgIv);
                imgIv.setVisibility(View.VISIBLE);
            }

        }

        @OnClick(R.id.frame)
        public void OnClick(View view){
            Intent intent=new Intent();
            intent.setClass(mContext, WebViewActivity.class);
            intent.putExtra("mTitle",titleTv.getText());
            intent.putExtra("mUrl",url);
            mContext.startActivity(intent);
        }
    }
    class GankViewHolder2 extends RecyclerView.ViewHolder {
        @BindView(R.id.item_title)
        TextView titleTv;
        @BindView(R.id.item_time)
        TextView timeTv;
        @BindView(R.id.item_author)
        TextView authorTv;
        private FrameLayout frameLayout;
        private String url;
        public GankViewHolder2(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            frameLayout= (FrameLayout) itemView;
        }

        public void bindData(GankBean.ResultsBean entity) {
            titleTv.setText(entity.getDesc());
            timeTv.setText(entity.getPublishedAt().replace("T"," ").substring(0,entity.getPublishedAt().indexOf(".")));
            authorTv.setText(entity.getWho());
            url=entity.getUrl();
        }

        @OnClick(R.id.frame)
        public void OnClick(View view){
            Intent intent=new Intent();
            intent.setClass(mContext, WebViewActivity.class);
            intent.putExtra("mTitle",titleTv.getText());
            intent.putExtra("mUrl",url);
            mContext.startActivity(intent);
        }
    }
}
