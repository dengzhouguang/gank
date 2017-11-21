package com.dzg.gank.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dzg.gank.R;
import com.dzg.gank.mvp.model.DianYingBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/8.
 */

public class DYDetailActivity extends AppCompatActivity {
    @BindView(R.id.title)
    TextView mTitleTv;
    @BindView(R.id.movie)
    ImageView mMovieIv;
    @BindView(R.id.ringht_content)
    TextView mRightContentTv;
    @BindView(R.id.download)
    TextView mDownloadTv;
    @BindView(R.id.translation)
    TextView mTranslationTv;
    @BindView(R.id.direction_actors)
    TextView mDirectionActorsTv;
    @BindView(R.id.summary)
    TextView mSummaryTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dianying_detail);
        ButterKnife.bind(this);
        final DianYingBean bean= (DianYingBean) getIntent().getExtras().getSerializable("bean");
        mTitleTv.setText(bean.getTitle());
        Glide.with(this).load(bean.getUrl()).into(mMovieIv);
        StringBuffer sb=new StringBuffer();
        if (bean.getDirector()!=null)
        sb.append(bean.getDirector()+"\r\n\r\n");
        if (bean.getTitle()!=null)
        sb.append(bean.getTitle()+"\r\n\n");
        if (bean.getCountry()!=null)
        sb.append(bean.getCountry()+"\r\n\n");
        if (bean.getTime()!=null)
        sb.append(bean.getTime()+"\r\n\n");
        if (bean.getLanguage()!=null)
        sb.append(bean.getLanguage()+"\r\n\n");
        if (bean.getScore()!=null)
        sb.append(bean.getScore()+"\r\n\n");
        mRightContentTv.setText(sb.toString());
        mDownloadTv.setText(bean.getDownUrl());
       mSummaryTv.setText(bean.getStory().replace("简介","").trim());
        mTranslationTv.setText(bean.getTranslation().replace("译名","").trim());
        mDirectionActorsTv.setText(bean.getDirector().replace("导演　","导演\r\n")+"\r\n"+bean.getActors());
        mDownloadTv.setTextIsSelectable(true);
        mDownloadTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(bean.getDownUrl()));
                startActivity(intent);
            }
        });
    }
}

