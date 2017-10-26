package com.dzg.gank.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
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

import com.dzg.gank.ItemDecoration.DividerItemDecoration;
import com.dzg.gank.R;
import com.dzg.gank.adapter.LoadAdapter;
import com.dzg.gank.listener.OnDouBanItemClickListener;
import com.dzg.gank.module.Movie;
import com.dzg.gank.ui.activity.DouBanDetailActivity;
import com.dzg.gank.util.CheckNetwork;
import com.dzg.gank.util.HttpUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class DouBanFragment extends Fragment {
    private boolean isFirst=true;
    private static int COUNT=20;
    private boolean isLoading=false;
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
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.douban,container,false);
        ButterKnife.bind(this,view);
        init();
        if (checkNetWork())
        getMovies(imgList.size(), imgList.size()+COUNT);
        return view;
    }
    public boolean checkNetWork(){
        if (!CheckNetwork.isNetworkConnected(getActivity())){
            mProgressIv.setVisibility(View.GONE);
            mProgressIv.clearAnimation();
            mJiazaiTv.setText("当前网络不可用，请检查网络！！！\r\n点击界面刷新.......");
            mJiazaiTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CheckNetwork.isNetworkConnected(getActivity())){
                        Toast.makeText(getActivity(),"网络不可用，请检查网络连接",Toast.LENGTH_SHORT).show();
                        mJiazaiTv.setText("当前网络不可用，请检查网络！！！\r\n点击界面刷新.......");
                        return;
                    }
                    mJiazaiTv.setText("正在加载.......");
                    mProgressIv.setVisibility(View.VISIBLE);
                    mProgressIv.startAnimation(rotate);
                    getMovies(imgList.size(), imgList.size()+COUNT);
                }
            });
            return false;
        }
        return true;
    }
    private void init() {
        imgList=new ArrayList<>();
        mAdapter = new LoadAdapter(getActivity());
        rotate= AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
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
                getMovies(imgList.size(),imgList.size()+10);
            }
        });
        mAdapter.setOnItemClickListener(new OnDouBanItemClickListener<Movie.SubjectsBean>() {
            ImageView mImageView = null;
            @Override
            public void setImageView(ImageView view) {
                mImageView=view;
            }

            @Override
            public void onClick(Movie.SubjectsBean subjectsBean, int position) {

                /*Intent intent = new Intent(context, OneMovieDetailActivity.class);
                intent.putExtra("bean", positionData);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(context,
                                imageView, CommonUtils.getString(R.string.transition_movie_img));//与xml文件对应
                ActivityCompat.startActivity(context, intent, options.toBundle());*/

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                mImageView, getActivity().getString(R.string.transition_movie_img));
                Intent intent=new Intent(getActivity(), DouBanDetailActivity.class);
                intent.putExtra("SubjectsBean",subjectsBean);
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        });
    }
    public boolean getMovies(int start, int end) {
        if (isLoading) return false;
        isLoading=true;
        HttpUtil.getDouBanService().getTopMovieByRetrofit(start, end)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Movie>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(Movie movie) {
                        List<Movie.SubjectsBean> subjects = movie.getSubjects();
                        mAdapter.addAll(subjects);

                        if (isFirst)
                        { mRecyclerView.setAdapter(mAdapter);
                            isFirst=false;
                        }
                        for (Movie.SubjectsBean bean: subjects){
                            imgList.add(bean.getImages().getLarge());}
                        mAdapter.notifyDataSetChanged();
                        isLoading=false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "发生错误了", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onComplete() {
                        rotate.cancel();
                        mLlwaitionLl.setVisibility(View.GONE);
                        mRecyclerView.refreshComplete();
                    }
                });
        return true;
    }
}

