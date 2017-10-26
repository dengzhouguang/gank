package com.dzg.gank.listener;

import android.widget.ImageView;

public interface OnDouBanItemClickListener<T> {
    public void setImageView(ImageView view);
    public void onClick(T t, int position);
}
