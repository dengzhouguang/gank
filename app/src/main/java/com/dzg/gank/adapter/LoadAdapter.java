package com.dzg.gank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzg.gank.App;
import com.dzg.gank.R;
import com.dzg.gank.listener.OnDouBanItemClickListener;
import com.dzg.gank.mvp.model.Movie;
import com.dzg.gank.util.ACache;
import com.dzg.gank.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoadAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<Movie.SubjectsBean> mList;
    private OnDouBanItemClickListener<Movie.SubjectsBean> onItemClickListener;
    public LoadAdapter(Context context) {
        mContext=context;
        mList=new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.layout_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder= (ViewHolder) holder;
         viewHolder.onBindViewHolder(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(OnDouBanItemClickListener<Movie.SubjectsBean> listener) {
        onItemClickListener=listener;
    }

    public void addAll(List<Movie.SubjectsBean> list){
        mList.addAll(list);
    }

    public List<Movie.SubjectsBean> getList() {
        return mList;
    }
     class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_tv)
        TextView mTextView;
        @BindView(R.id.item_iv)
        ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void onBindViewHolder(final Movie.SubjectsBean resultsBean, final int position) {
            final ACache cache=ACache.get(mContext);
            if (position % 2 == 0) {
                DensityUtil.setViewMargin(itemView, false, 3, 3, 3, 3);
            } else {
                DensityUtil.setViewMargin(itemView, false, 3, 3, 3, 3);
            }

            mTextView.setText(resultsBean.getTitle());
            Glide.with(App.getInstance()).load(resultsBean.getImages().getLarge()).into(mImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.setImageView(mImageView);
                        onItemClickListener.onClick(resultsBean, position);
                    }
                }
            });
        }
    }
}
