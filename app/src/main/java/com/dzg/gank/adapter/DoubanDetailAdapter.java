package com.dzg.gank.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzg.gank.R;
import com.dzg.gank.module.MovieDetail;
import com.dzg.gank.ui.webview.WebViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dengzhouguang on 2017/10/21.
 */

public class DoubanDetailAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private MovieDetail mMovieDetail;
    private int mCount;

    public DoubanDetailAdapter(Context context, MovieDetail movieDetail) {
        mContext = context;
        mMovieDetail = movieDetail;
        mCount = mMovieDetail.getCasts().size() + mMovieDetail.getDirectors().size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_doubandetail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView mImageView;
        @BindView(R.id.name)
        TextView mNameTv;
        @BindView(R.id.identity)
        TextView mIdentityTv;
        private String mUrl;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position < mMovieDetail.getDirectors().size()) {
                Glide.with(mContext).load(mMovieDetail.getDirectors().get(position).getAvatars().getLarge()).into(mImageView);
                mNameTv.setText(mMovieDetail.getDirectors().get(position).getName());
                mIdentityTv.setText("导演");
                mUrl=mMovieDetail.getDirectors().get(position).getAlt();
            } else {
                Glide.with(mContext).load(mMovieDetail.getCasts().get(position - mMovieDetail.getDirectors().size()).getAvatars().getLarge()).into(mImageView);
                mNameTv.setText(mMovieDetail.getCasts().get(position - mMovieDetail.getDirectors().size()).getName());
                mIdentityTv.setText("演员");
                mUrl=mMovieDetail.getCasts().get(position - mMovieDetail.getDirectors().size()).getAlt();
            }
        }

        @OnClick(R.id.container)
        public void OnClick(View view) {
                Intent intent = new Intent();
                intent.setClass(mContext, WebViewActivity.class);
                intent.putExtra("mTitle", mNameTv.getText());
                intent.putExtra("mUrl", mUrl);
                mContext.startActivity(intent);
        }
    }
}
