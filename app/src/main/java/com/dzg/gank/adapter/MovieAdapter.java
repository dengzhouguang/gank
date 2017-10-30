package com.dzg.gank.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestOptions;
import com.dzg.gank.R;
import com.dzg.gank.module.DianYingBean;
import com.dzg.gank.ui.activity.DYDetailActivity;
import com.dzg.gank.util.DensityUtil;
import com.sunfusheng.glideimageview.GlideImageView;
import com.sunfusheng.glideimageview.progress.CircleProgressView;
import com.sunfusheng.glideimageview.progress.OnGlideImageViewListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<DianYingBean> mList;
    private DianYingBean mTempBean = null;

    public MovieAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void addAll(List<DianYingBean> list) {
        Log.e("error", list.size() + "");
        if (list.size() > 0) {
            if (list.size() % 2 == 0) {
                if (mTempBean != null) {
                    mList.add(mTempBean);
                    mTempBean = list.remove(list.size() - 1);
                }
            } else {
                if (mTempBean != null) {
                    mList.add(mTempBean);
                } else {
                    mTempBean = list.remove(list.size() - 1);
                }
            }
            mList.addAll(list);
        }
//        ListUtil.removeDuplicate(list)
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dianying_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder holder1 = (ViewHolder) holder;
        ((ViewHolder) holder).onBindViewHolder(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<DianYingBean> getList() {
        return mList;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_tv)
        TextView mTextView;
        /*@BindView(R.id.item_iv)
        ImageView mImageView;*/
        @BindView(R.id.item_iv)
        GlideImageView mGlideImageView;
        @BindView(R.id.item_progress)
        CircleProgressView mCircleProgressView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBindViewHolder(final DianYingBean bean, final int position) {
            if (position % 2 == 0) {
                DensityUtil.setViewMargin(itemView, false, 3, 3, 3, 0);
            } else {
                DensityUtil.setViewMargin(itemView, false, 3, 3, 3, 0);
            }
            mTextView.setText(bean.getTitle());
            RequestOptions requestOptions = mGlideImageView.requestOptions(R.color.placeholder_color).centerCrop();
            mGlideImageView.load(bean.getUrl(), requestOptions).listener(new OnGlideImageViewListener() {
                @Override
                public void onProgress(int percent, boolean isDone, GlideException exception) {
                    if (exception != null && !TextUtils.isEmpty(exception.getMessage())) {
//                        Toast.makeText(App.getInstance(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error", exception.getMessage());
                    }
                    mCircleProgressView.setProgress(percent);
                    mCircleProgressView.setVisibility(isDone ? View.GONE : View.VISIBLE);
                }
            });
//            RxBus.getInstance().post(Constants.EVENT_FLAG,new MovieWrapper(mImageView,bean.getUrl()));
//            Glide.with(mContext).load(bean.getUrl()).into(mImageView);
            /*Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> e) throws Exception {
                    RequestOptions requestOptions = mGlideImageView.requestOptions(R.color.placeholder_color).centerCrop();
                    mGlideImageView.load(bean.getUrl(), requestOptions).listener(new OnGlideImageViewListener() {
                        @Override
                        public void onProgress(int percent, boolean isDone, GlideException exception) {
                            if (exception != null && !TextUtils.isEmpty(exception.getMessage())) {
//                        Toast.makeText(App.getInstance(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("error", exception.getMessage());
                            }
                            Log.e("percent",percent+"   "+isDone);
                            mCircleProgressView.setProgress(percent);
                            mCircleProgressView.setVisibility(isDone ? View.GONE : View.VISIBLE);
                        }
                    });
                }
            }).subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe();*/

           /* GlideImageLoader imageLoader = mGlideImageView.getImageLoader();
            imageLoader.setOnGlideImageViewListener(bean.getUrl(), new OnGlideImageViewListener() {
                @Override
                public void onProgress(int percent, boolean isDone, GlideException exception) {
                    if (exception != null && !TextUtils.isEmpty(exception.getMessage())) {
                        Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    mCircleProgressView.setProgress(percent);
                    mCircleProgressView.setVisibility(isDone ? View.GONE : View.VISIBLE);
                }
            });
            RequestOptions requestOptions = mGlideImageView.requestOptions(R.color.placeholder_color).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
            imageLoader.requestBuilder(bean.getUrl(), requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mGlideImageView);*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Bundle bundle = new Bundle();
                    bundle.putSerializable("bean", bean);
                    Intent intent = new Intent(mContext, DYDetailActivity.class);
                    intent.putExtras(bundle);
                    ActivityOptionsCompat compat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation((Activity) mContext, mGlideImageView, mContext.getString(R.string.transitional_image));
                    ActivityCompat.startActivity(mContext, intent, compat.toBundle());
                }
            });
        }
    }
}
