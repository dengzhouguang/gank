package com.dzg.gank.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dzg.gank.R;
import com.dzg.gank.listener.OnItemClickListener;
import com.dzg.gank.mvp.model.FuLiBean;
import com.dzg.gank.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FuLiAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<FuLiBean.ResultsBean> mList;
    private OnItemClickListener<FuLiBean.ResultsBean> onItemClickListener;

    public FuLiAdapter(Context context) {
        mContext=context;
        mList=new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myholder= (ViewHolder) holder;
        myholder.onBindViewHolder(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addAll(List<FuLiBean.ResultsBean> list ){
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<FuLiBean.ResultsBean> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

     class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        @BindView(R.id.item_tv)
        TextView itemTv;
        @BindView(R.id.item_iv)
        ImageView itemIv;
        ViewHolder(View view) {
            super(view);
            itemView=view;
            ButterKnife.bind(this,view);
        }


        public void onBindViewHolder(final FuLiBean.ResultsBean resultsBean, final int position) {
//            final ACache cache=ACache.get(mContext);
            if (position % 2 == 0) {
                DensityUtil.setViewMargin(itemView, false, 3, 0, 3, 3);
            } else {
                DensityUtil.setViewMargin(itemView, false, 3, 3, 3, 3);
            }
            itemTv.setText(resultsBean.getDesc());
//            if (cache.getAsDrawable(resultsBean.getDesc())==null) {
                Glide.with(mContext).load(resultsBean.getUrl()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        cache.put(resultsBean.getDesc(), ImageUtil.drawable2Bitmap(resource));
                        return false;
                    }
                }).into(itemIv);
            /*}else {
                Glide.with(App.getInstance()).load(cache.getAsBinary(resultsBean.getDesc())).into(itemIv);
            }*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(resultsBean, position);
                    }
                }
            });
        }
    }
}
