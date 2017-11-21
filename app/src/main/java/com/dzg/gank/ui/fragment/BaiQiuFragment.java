package com.dzg.gank.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dzg.gank.App;
import com.dzg.gank.R;
import com.dzg.gank.adapter.RecyclerViewAdapter;
import com.dzg.gank.injector.component.ApplicationComponent;
import com.dzg.gank.injector.component.BaiQiuComponent;
import com.dzg.gank.injector.component.DaggerBaiQiuComponent;
import com.dzg.gank.injector.module.BaiQiuModule;
import com.dzg.gank.injector.module.FragmentModule;
import com.dzg.gank.listener.ItemTouchHelperCallback;
import com.dzg.gank.mvp.contract.BaiQiuContract;
import com.dzg.gank.util.ToastUtil;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dengzhouguang on 2017/10/23.
 */

public class BaiQiuFragment extends RxFragment implements BaiQiuContract.View{
    @BindView(R.id.recycler_view_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout_recycler_view)
    SwipeRefreshLayout swipeRefreshLayout;
    @Inject
    BaiQiuContract.Presenter mPresenter;
    private boolean loading;
    private RecyclerViewAdapter adapter;
    private List<String> mData;
    private int mPage = 1;
    private boolean mIsNetWork=true;
    private static BaiQiuFragment instance=null;

    public static BaiQiuFragment getInstance() {
        if (instance == null) {
            instance = new BaiQiuFragment();
        }
        return instance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baiqiu, container, false);
        ButterKnife.bind(this, view);
        initView();
        injectDependences();
        mPresenter.attachView(this);
        mPresenter.initData();
        return view;
    }
    private void injectDependences() {
        ApplicationComponent applicationComponent = App.getInstance().getApplicationComponent();

       BaiQiuComponent component= DaggerBaiQiuComponent.builder()
               .applicationComponent(applicationComponent)
               .baiQiuModule(new BaiQiuModule())
               .fragmentModule(new FragmentModule(this))
               .build();
       component.inject(this);
    }
    private void initView() {
        mData = new ArrayList<>();
        if (getScreenWidthDp() >= 1200) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else if (getScreenWidthDp() >= 800) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
        adapter = new RecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
        adapter.setItems(mData);
        adapter.addFooter();
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=1;
                mPresenter.initData();
            }
        });

        mRecyclerView.addOnScrollListener(scrollListener);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            float y1,y2;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //当手指按下的时候
                    y1 = event.getY();
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    //当手指离开的时候
                    y2 = event.getY();
                    if(y1 - y2 > 50) {
                        if (!mIsNetWork) {
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                            if (!loading && mPage != 1 && linearLayoutManager.getItemCount() == (linearLayoutManager.findLastVisibleItemPosition() + 1)) {
                                adapter.addFooter();
                                loading=true;
                                mPresenter.loadData(mPage+"");
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (!loading && adapter.getItemCount()>1 && linearLayoutManager.getItemCount() == (linearLayoutManager.findLastVisibleItemPosition() + 1)) {
                adapter.addFooter();
                loading = true;
                mPresenter.loadData(mPage+"");
               /* Snackbar.make(mRecyclerView, getString(R.string.no_more_data), Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        loading = false;
                        adapter.addFooter();*/
            }
        }
    };


    private int getScreenWidthDp() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
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
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onDestroy() {
        if (instance!=null)
            instance=null;
        super.onDestroy();
    }

    @Override
    public void showData(List<String> list) {
        adapter.removeFooter();
        adapter.setItems(list);
        swipeRefreshLayout.setRefreshing(false);
        mIsNetWork=true;
    }

    @Override
    public void showError() {
        ToastUtil.showToast("网络不可用，请检查网络情况！");
        adapter.removeFooter();
        mIsNetWork=false;
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void complete() {
        mPage++;
    }

    @Override
    public void showLoadData(List<String> list) {
        mIsNetWork=true;
        adapter.removeFooter();
        adapter.addItems(list);
    }

    @Override
    public void showLoadError() {
        ToastUtil.showToast("网络不可用，请检查网络情况！");
        mIsNetWork=false;
        adapter.removeFooter();
        swipeRefreshLayout.setRefreshing(false);
        loading=false;
    }

    @Override
    public void loadComplete() {
        mPage++;
        loading=false;
    }
}
