package com.dzg.gank.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
import com.dzg.gank.adapter.GankAdapter;
import com.dzg.gank.injector.component.ApplicationComponent;
import com.dzg.gank.injector.component.DaggerGankComponent;
import com.dzg.gank.injector.component.GankComponent;
import com.dzg.gank.injector.module.FragmentModule;
import com.dzg.gank.injector.module.GankModule;
import com.dzg.gank.mvp.contract.GankContract;
import com.dzg.gank.mvp.model.GankBean;
import com.dzg.gank.util.CheckNetwork;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dengzhouguang on 2017/10/13.
 */

public class GankFragment extends RxFragment implements GankContract.View{
    @BindView(R.id.main_recycler_view)
    XRecyclerView mRecyclerView;
    @BindView(R.id.llwaiting)
    LinearLayout mLinearlayout;
    @BindView(R.id.jiazai)
    TextView mJiazaiTv;
    @BindView(R.id.progress)
    ImageView mProgress;
    private Animation mRotate;
    private GankAdapter mAdapter;
    private int mPage=1;
    private static GankFragment instance=null;
    @Inject
    GankContract.Presenter mPresenter;
    public static GankFragment getInstance() {
        if (instance == null) {
            instance = new GankFragment();
        }
        return instance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_gank,null);
        ButterKnife.bind(this,view);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        injectDependences();
        mPresenter.attachView(this);
        initView();
    }
    private void injectDependences() {
        ApplicationComponent applicationComponent = App.getInstance().getApplicationComponent();
        GankComponent component= DaggerGankComponent.builder()
                .applicationComponent(applicationComponent)
                .gankModule(new GankModule())
                .fragmentModule(new FragmentModule(this))
                .build();
        component.inject(this);
    }
    public void initView(){
        mRotate= AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
        mRotate.setInterpolator(new LinearInterpolator());
        mProgress.startAnimation(mRotate);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter=new GankAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.loadMoreComplete();
            }

            @Override
            public void onLoadMore() {
                mPresenter.loadData(mPage+"");
                mRecyclerView.loadMoreComplete();
            }
        });
        mRecyclerView.setPullRefreshEnabled(false);
        mPresenter.loadData(mPage+"");
    }


    @Override
    public void loadDataSuccess(GankBean bean) {
        mLinearlayout.setVisibility(View.GONE);
        mAdapter.add(bean.getResults());
    }

    @Override
    public void loadDataError(Throwable e) {
        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadDataComplete() {

    }

    @Override
    public void loadDataFinish() {
        mAdapter.notifyDataSetChanged();
        ++mPage;
    }

    public boolean checkNetWork() {
        if (!CheckNetwork.isNetworkConnected(getActivity())) {
            mProgress.setVisibility(View.GONE);
            mProgress.clearAnimation();
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
                    mProgress.setVisibility(View.VISIBLE);
                    mProgress.startAnimation(mRotate);
                    mPresenter.loadData(mPage+"");
                }
            });
            return false;
        }
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
