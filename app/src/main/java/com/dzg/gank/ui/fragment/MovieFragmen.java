package com.dzg.gank.ui.fragment;

import android.content.Context;
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
import com.dzg.gank.App;
import com.dzg.gank.R;
import com.dzg.gank.adapter.MovieAdapter;
import com.dzg.gank.injector.component.ApplicationComponent;
import com.dzg.gank.injector.component.DaggerMovieComponent;
import com.dzg.gank.injector.component.MovieComponent;
import com.dzg.gank.injector.module.FragmentModule;
import com.dzg.gank.injector.module.MovieModule;
import com.dzg.gank.mvp.contract.MovieContract;
import com.dzg.gank.mvp.model.DianYingBean;
import com.dzg.gank.ui.view.ItemDecoration.DividerGridItemDecoration;
import com.dzg.gank.util.CheckNetwork;
import com.dzg.gank.util.ToastUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieFragmen extends RxFragment implements MovieContract.View {
    private boolean isLoading = false;
    private MovieAdapter mAdapter;
    private Animation mRotate;
    private int mPage = 0;
    private static String BASE_URL = "http://www.ygdy8.net/html/gndy/dyzz/list_23_";
    private static String Host = "http://www.ygdy8.net";
    private Context mContext;
    private static MovieFragmen instance = null;

    public static MovieFragmen getInstance() {
        if (instance == null) {
            instance = new MovieFragmen();
        }
        return instance;
    }

    @BindView(R.id.llwaiting)
    LinearLayout mLlwaitionLl;
    @BindView(R.id.jiazai)
    TextView mJiazaiTv;
    @BindView(R.id.progress)
    ImageView mProgressIv;
    @BindView(R.id.recyclerview)
    XRecyclerView mRecyclerView;
    @Inject
    MovieContract.Presenter mPresenter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dianying, container, false);
        ButterKnife.bind(this, view);
        initView();
        injectDependences();
        mPresenter.attachView(this);
        if (checkNetWork())
            mPresenter.loadData(++mPage);
        return view;
    }

    private void injectDependences() {
        ApplicationComponent applicationComponent = App.getInstance().getApplicationComponent();
        MovieComponent component= DaggerMovieComponent.builder()
                .applicationComponent(applicationComponent)
                .movieModule(new MovieModule())
                .fragmentModule(new FragmentModule(this))
                .build();
        component.inject(this);
    }
    public boolean checkNetWork() {
        if (!CheckNetwork.isNetworkConnected(getActivity())) {
            mProgressIv.setVisibility(View.GONE);
            mProgressIv.clearAnimation();
            ToastUtil.showToast("当前网络不可用，将加载上次使用的数据！");
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
                    mProgressIv.startAnimation(mRotate);
                    mPresenter.loadData(++mPage);
                }
            });
            return false;
        }
        return true;
    }

    private void initView() {
        mAdapter = new MovieAdapter(getActivity());
        mRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        mRotate.setInterpolator(new LinearInterpolator());
        mProgressIv.startAnimation(mRotate);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                mPresenter.loadData(++mPage);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void loadDataSuccess(Document document) {
        Elements elements = document.body().select("a.ulink");
        List<DianYingBean> list = new ArrayList<>();
        int size = elements.size();
        for (int i = 0; i < size; i++) {
            String url = elements.get(i).attr("href");
            mPresenter.getDianYingDetail(Host + url, i);
        }
    }

    @Override
    public void loadDataError(Throwable e) {
        isLoading = false;
        ToastUtil.showToast("网络发生异常，请检查网络情况。");
    }

    @Override
    public void loadDataComplete() {
        isLoading = false;
    }

    @Override
    public void loadDetailSuccess(Document document,int index) {
        Elements div = document.select("div.co_content8");
        Elements img = div.select("img[src]");
        String imgURL = img.get(0).attr("src");
//        String sumURL = img.get(1).attr("src");
        String downURL = div.select("td").first().text();
        Elements content = div.select("span");
        content.select("center").remove();
        content.select("font").remove();
        content.select("a[href]").remove();
        String text = content.text();
        DianYingBean bean = new DianYingBean();
        bean.setUrl(imgURL);
        bean.setDownUrl(downURL);

        int end = -1;
        int pre = 0;
        if ((pre = text.indexOf("◎译　　名")) > 0)
            bean.setTranslation(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎片　　名")) > 0)
            bean.setName(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎国　　家")) > 0)
            bean.setCountry(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎类　　别")) > 0)
            bean.setType(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎语　　言")) > 0)
            bean.setLanguage(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎字　　幕")) > 0)
            bean.setSubtitle(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎IMDb评分")) > 0)
            bean.setScore(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎文件格式")) > 0)
            bean.setFormat(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎视频尺寸")) > 0)
            bean.setMeasure(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎文件大小")) > 0)
            bean.setSize(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎片　　长")) > 0)
            bean.setTime(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎导　　演")) > 0)
            bean.setDirector(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        if ((pre = text.indexOf("◎简　　介")) > 0)
            bean.setStory(text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length()).replace("　　", "").replace("◎", ""));
        /*if (text.indexOf("]") > 0)
            bean.setTitle(text.substring(0, text.indexOf("]")));
        else */if (bean.getTranslation() != null)
            bean.setTitle(bean.getTranslation().replace("译名　", ""));
        else if (bean.getName() != null)
            bean.setTitle(bean.getName().replace("片名　", ""));
        String actor = null;
        if ((pre = text.indexOf("◎主　　演")) > 0) {
            actor = text.substring(pre, (end = text.indexOf("◎", pre + 1)) > 0 ? end : text.length());
            actor = actor.replaceAll(" 　　　　　　", "\r\n");
            bean.setActors(actor.replace("◎主　　演　", "主演\r\n"));
        }
        mAdapter.add(bean);
        if (mAdapter.getItemCount() % 2 == 0)
            mAdapter.notifyDataSetChanged();
        if (index > 6) {
            if (mLlwaitionLl.getVisibility() == View.VISIBLE) {
                mLlwaitionLl.clearAnimation();
                mLlwaitionLl.setVisibility(View.GONE);
            }
            mRecyclerView.loadMoreComplete();
            mRecyclerView.refreshComplete();
        }
    }

    @Override
    public boolean setLoading() {
        if (isLoading) return false;
        isLoading = true;
        return true;
    }
   /* public void register(){
        RxBus.getInstance().tObservable(Constants.EVENT_FLAG,MovieWrapper.class)
                .filter(new Predicate() {
                    @Override
                    public boolean test(Object o) throws Exception {
                        return o!=null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieWrapper>(){

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MovieWrapper movieWrapper) {
                        Glide.with(mContext).load(movieWrapper.getUrl()).into(movieWrapper.getGlideImageView());
//                        mAdapter.notifyDataSetChanged();
                        *//*RequestOptions requestOptions = movieWrapper.getGlideImageView().requestOptions(R.color.placeholder_color).centerCrop();
                        movieWrapper.getGlideImageView().load(movieWrapper.getUrl(), requestOptions).listener(new OnGlideImageViewListener() {
                            @Override
                            public void onProgress(int percent, boolean isDone, GlideException exception) {
                                if (exception != null && !TextUtils.isEmpty(exception.getMessage())) {
//                        Toast.makeText(App.getInstance(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("error", exception.getMessage());
                                }
                                Log.e("percent",percent+"   "+isDone);
                                movieWrapper.getCircleProgressView().setProgress(percent);
                                movieWrapper.getCircleProgressView().setVisibility(isDone ? View.GONE : View.VISIBLE);
                            }
                        });*//*
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }*/
    }
