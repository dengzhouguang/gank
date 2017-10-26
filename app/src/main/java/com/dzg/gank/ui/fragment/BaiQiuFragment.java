package com.dzg.gank.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.dzg.gank.R;
import com.dzg.gank.adapter.RecyclerViewAdapter;
import com.dzg.gank.listener.ItemTouchHelperCallback;
import com.dzg.gank.util.HttpUtil;
import com.dzg.gank.util.ToastUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by dengzhouguang on 2017/10/23.
 */

public class BaiQiuFragment extends Fragment {
    @BindView(R.id.recycler_view_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout_recycler_view)
    SwipeRefreshLayout swipeRefreshLayout;

    private boolean loading;
    private RecyclerViewAdapter adapter;
    private List<String> mData;
    private int color = 0;
    private int mPage = 1;
    private boolean mIsNetWork=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baiqiu, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    private void initData() {
        HttpUtil.getBaiQiuService().getData(mPage + "")
                .map(new Function<ResponseBody, List<String>>() {
                    @Override
                    public List<String> apply(@NonNull ResponseBody responseBody) throws Exception {
                        List<String> list = new ArrayList<String>();
                        Document doc = null;
                        try {
                            doc = Jsoup.parse(responseBody.string());
                            Elements els = doc.select("a.contentHerf");
                            for (int i = 0; i < els.size(); i++) {
                                Element el = els.get(i);
                                String txt = el.text();
                                String url = el.attr("href");
                                if (txt.endsWith("查看全文")) {
                                    Document document = Jsoup.connect("https://www.qiushibaike.com" + url).get();
                                    Elements elements = document.select("div.content");
                                    list.add(elements.text());
                                } else {
                                    list.add(txt);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return list;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<String> strings) {
                        adapter.removeFooter();
                        adapter.setItems(strings);
                        swipeRefreshLayout.setRefreshing(false);
                        mIsNetWork=true;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtil.showToast("网络不可用，请检查网络情况！");
                        adapter.removeFooter();
                        mIsNetWork=false;
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        mPage++;
                    }
                });
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
                initData();
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
                                loadData();
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
                loadData();
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

    private void loadData() {
        mData = new ArrayList<>();
        HttpUtil.getBaiQiuService().getData(mPage + "")
                .map(new Function<ResponseBody, List<String>>() {
                    @Override
                    public List<String> apply(@NonNull ResponseBody responseBody) throws Exception {
                        List<String> list = new ArrayList<String>();
                        Document doc = null;
                        try {
                            doc = Jsoup.parse(responseBody.string());
                            Elements els = doc.select("a.contentHerf");
                            for (int i = 0; i < els.size(); i++) {
                                Element el = els.get(i);
                                String txt = el.text();
                                String url = el.attr("href");
                                if (txt.endsWith("查看全文")) {
                                    Document document = Jsoup.connect("https://www.qiushibaike.com" + url).get();
                                    Elements elements = document.select("div.content");
                                    list.add(elements.text());
                                } else {
                                    list.add(txt);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return list;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<String> strings) {
                        mIsNetWork=true;
                        adapter.removeFooter();
                        adapter.addItems(strings);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtil.showToast("网络不可用，请检查网络情况！");
                        mIsNetWork=false;
                        adapter.removeFooter();
                        swipeRefreshLayout.setRefreshing(false);
                        loading=false;
                    }

                    @Override
                    public void onComplete() {
                        mPage++;
                        loading=false;
                    }
                });
    }

}
