package com.dzg.gank.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dzg.gank.ItemDecoration.DividerItemDecoration;
import com.dzg.gank.R;
import com.dzg.gank.adapter.FuLiAdapter;
import com.dzg.gank.listener.OnItemClickListener;
import com.dzg.gank.module.FuLiBean;
import com.dzg.gank.ui.activity.ViewBigImageActivity;
import com.dzg.gank.util.CheckNetwork;
import com.dzg.gank.util.HttpUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/6.
 */

public class FuLiFragment extends RxFragment {
    @BindView(R.id.recyclerview)
    XRecyclerView mRecyclerView;
    @BindView(R.id.progress)
    ImageView mProgress;
    @BindView(R.id.jiazai)
    TextView jiazai;
    @BindView(R.id.llwaiting)
    LinearLayout mLayout;
    private boolean isFirst = true;
    private static int COUNT = 20;
    private boolean isLoading = false;
    private FuLiAdapter mAdapter;
    private ArrayList<String> imgList = new ArrayList<>();
    private Animation rotate;
    private int page = 1;
    private static FuLiFragment instance=null;
    public static FuLiFragment getInstance() {
        if (instance == null) {
            instance = new FuLiFragment();
        }
        return instance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuli, null);
        ButterKnife.bind(this, view);
        init();
        if (checkNetWork())
            getFuLi(page);
        return view;
    }

    private void init() {
        mAdapter = new FuLiAdapter(getActivity());
        rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        rotate.setInterpolator(new LinearInterpolator());
        mProgress.startAnimation(rotate);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                getFuLi(++page);
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener<FuLiBean.ResultsBean>() {
            @Override
            public void onClick(FuLiBean.ResultsBean subjectsBean, int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("selet", 2);// 2,大图显示当前页数，1,头像，不显示页数
                bundle.putInt("code", position);//第几张
                bundle.putStringArrayList("imageuri", imgList);
                Intent intent = new Intent(getActivity(), ViewBigImageActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public boolean checkNetWork() {
        if (!CheckNetwork.isNetworkConnected(getActivity())) {
            mProgress.setVisibility(View.GONE);
            mProgress.clearAnimation();
            jiazai.setText("当前网络不可用，请检查网络！！！\r\n点击界面刷新.......");
            jiazai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CheckNetwork.isNetworkConnected(getActivity())) {
                        Toast.makeText(getActivity(), "网络不可用，请检查网络连接", Toast.LENGTH_SHORT).show();
                        jiazai.setText("当前网络不可用，请检查网络！！！\r\n点击界面刷新.......");
                        return;
                    }
                    jiazai.setText("正在加载.......");
                    mProgress.setVisibility(View.VISIBLE);
                    mProgress.startAnimation(rotate);
                    getFuLi(page);
                }
            });
            return false;
        }
        return true;
    }

    public boolean getFuLi(int page) {
        if (isLoading) return false;
        isLoading = true;

        HttpUtil.getFuliService().getFuLiData(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<FuLiBean>bindUntilEvent(FragmentEvent.STOP))
                .subscribe(new Observer<FuLiBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(FuLiBean bean) {
                        List<FuLiBean.ResultsBean> subjects = bean.getResults();
                        mAdapter.addAll(subjects);

                        if (isFirst) {
                            mRecyclerView.setAdapter(mAdapter);
                            isFirst = false;
                        }
                        for (FuLiBean.ResultsBean resultsBeanbean : subjects) {
//                            Log.e("youdu",resultsBeanbean.getUrl());
                            imgList.add(resultsBeanbean.getUrl());
                        }
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        rotate.cancel();
                        mLayout.setVisibility(View.GONE);
                        mRecyclerView.refreshComplete();
                    }
                });
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();
        Glide.with(getActivity()).resumeRequests();
    }

    @Override
    public void onPause() {
        super.onPause();
        Glide.with(getActivity()).pauseRequests();
    }

    @Override
    public void onDestroy() {
        if (instance!=null)
            instance=null;
        super.onDestroy();
    }
}
