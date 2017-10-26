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
import com.dzg.gank.listener.OnItemClickListener;
import com.dzg.gank.module.DianYingBean;
import com.dzg.gank.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private OnItemClickListener<DianYingBean> onItemClickListener;
    private List<DianYingBean> mList;
    public MovieAdapter(Context context) {
        mContext=context;
        mList=new ArrayList<>();
    }
    public void addAll(List<DianYingBean> list){
        mList.addAll(list);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.dianying_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ViewHolder holder1= (ViewHolder) holder;
        ((ViewHolder) holder).onBindViewHolder(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<DianYingBean> getList() {
        return mList;
    }

    public void setOnItemClickListener(OnItemClickListener<DianYingBean> listener) {
        onItemClickListener=listener;
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
        public void onBindViewHolder(final DianYingBean bean, final int position) {
            if (position % 2 == 0) {
                DensityUtil.setViewMargin(itemView, false, 3, 3, 3, 0);
            } else {
                DensityUtil.setViewMargin(itemView, false, 3, 3, 3, 0);
            }
            mTextView.setText(bean.getTitle());
            Glide.with(App.getInstance()).load(bean.getUrl()).into(mImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(bean, position);
                    }
                }
            });
        }
    }
}
