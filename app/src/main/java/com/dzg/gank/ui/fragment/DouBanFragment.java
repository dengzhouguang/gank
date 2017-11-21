package com.dzg.gank.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.dzg.gank.App;
import com.dzg.gank.R;
import com.dzg.gank.adapter.LoadAdapter;
import com.dzg.gank.injector.component.ApplicationComponent;
import com.dzg.gank.injector.component.DaggerDouBanComponent;
import com.dzg.gank.injector.component.DouBanComponent;
import com.dzg.gank.injector.module.DouBanModule;
import com.dzg.gank.injector.module.FragmentModule;
import com.dzg.gank.listener.OnDouBanItemClickListener;
import com.dzg.gank.mvp.contract.DouBanContract;
import com.dzg.gank.mvp.model.Movie;
import com.dzg.gank.ui.activity.DouBanDetailActivity;
import com.dzg.gank.ui.view.ItemDecoration.DividerItemDecoration;
import com.dzg.gank.util.CheckNetwork;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DouBanFragment extends RxFragment implements DouBanContract.View {
    private boolean isFirst = true;
    private static int COUNT = 20;
    private boolean isLoading = false;
    private LoadAdapter mAdapter;
    private ArrayList<String> imgList;
    private Animation rotate;
    @BindView(R.id.llwaiting)
    LinearLayout mLlwaitionLl;
    @BindView(R.id.jiazai)
    TextView mJiazaiTv;
    @BindView(R.id.progress)
    ImageView mProgressIv;
    @BindView(R.id.recyclerview)
    XRecyclerView mRecyclerView;
    @Inject
    DouBanContract.Presenter mPresenter;

    private static DouBanFragment instance = null;

    public static DouBanFragment getInstance() {
        if (instance == null) {
            instance = new DouBanFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.douban, container, false);
        ButterKnife.bind(this, view);
        initView();
        injectDependences();
        mPresenter.attachView(this);
        if (checkNetWork()) {
            mPresenter.loadData(imgList.size(), imgList.size() + COUNT);
        }
        return view;
    }
    private void injectDependences() {
        ApplicationComponent applicationComponent = App.getInstance().getApplicationComponent();
        DouBanComponent component= DaggerDouBanComponent.builder()
                .applicationComponent(applicationComponent)
                .douBanModule(new DouBanModule())
                .fragmentModule(new FragmentModule(this))
                .build();
        component.inject(this);
    }
    public boolean checkNetWork() {
        if (!CheckNetwork.isNetworkConnected(getActivity())) {
            mProgressIv.setVisibility(View.GONE);
            mProgressIv.clearAnimation();
            mJiazaiTv.setText("当前网络不可用，请检查网络！！！\r\n点击界面刷新.......");
            mJiazaiTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CheckNetwork.isNetworkConnected(getActivity())) {
                        Toast.makeText(getActivity(), "网络不可用，请检查网络连接", Toast.LENGTH_SHORT).show();
                        mJiazaiTv.setText("当前网络不可用，请检查网络！！！\r\n点击界面刷新.......");
                        return;
                    }
                    mJiazaiTv.setText("正在加载.......");
                    mProgressIv.setVisibility(View.VISIBLE);
                    mProgressIv.startAnimation(rotate);
                    mPresenter.loadData(imgList.size(), imgList.size() + COUNT);
                }
            });
            return false;
        }
        return true;
    }

    private void initView() {
        imgList = new ArrayList<>();
        mAdapter = new LoadAdapter(getActivity());
        rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        rotate.setInterpolator(new LinearInterpolator());
        mProgressIv.startAnimation(rotate);
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
                mPresenter.loadData(imgList.size(), imgList.size() + 10);
            }
        });
        mAdapter.setOnItemClickListener(new OnDouBanItemClickListener<Movie.SubjectsBean>() {
            ImageView mImageView = null;

            @Override
            public void setImageView(ImageView view) {
                mImageView = view;
            }

            @Override
            public void onClick(Movie.SubjectsBean subjectsBean, int position) {
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                mImageView, getActivity().getString(R.string.transition_movie_img));
                Intent intent = new Intent(getActivity(), DouBanDetailActivity.class);
                intent.putExtra("SubjectsBean", subjectsBean);
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        });
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
        if (instance != null)
            instance = null;
        super.onDestroy();
    }

    @Override
    public void loadDataSuccess(Movie movie) {
        List<Movie.SubjectsBean> subjects = movie.getSubjects();
        mAdapter.addAll(subjects);

        if (isFirst) {
            mRecyclerView.setAdapter(mAdapter);
            isFirst = false;
        }
        for (Movie.SubjectsBean bean : subjects) {
            imgList.add(bean.getImages().getLarge());
        }
        mAdapter.notifyDataSetChanged();
        isLoading = false;
    }

    @Override
    public void loadDataError() {
        Toast.makeText(getActivity(), "发生错误了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadDateComplete() {
        rotate.cancel();
        mLlwaitionLl.setVisibility(View.GONE);
        mRecyclerView.refreshComplete();
    }

    @Override
    public boolean setLoading() {
        if (isLoading) return false;
        isLoading = true;
        return true;
    }
}

