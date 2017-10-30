package com.dzg.gank.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dzg.gank.R;
import com.dzg.gank.adapter.SearchAdapter;
import com.dzg.gank.adapter.SearchResultAdapter;
import com.dzg.gank.module.GankBean;
import com.dzg.gank.module.Type;
import com.dzg.gank.util.CheckNetwork;
import com.dzg.gank.util.HttpUtil;
import com.dzg.gank.util.SharedPreference;
import com.dzg.gank.util.Utils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dengzhouguang on 2017/10/13.
 */

public class SearchFragment extends Fragment {
    @BindView(R.id.main_recycler_view)
    XRecyclerView mRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.progress)
    ImageView mProgress;
    @BindView(R.id.jiazai)
    TextView mJiazaiTv;
    @BindView(R.id.llwaiting)
    LinearLayout mLayout;
    private SearchResultAdapter mAdapter;
    private int mPage=1;
    private ArrayList<String> mList;
    private String mSearchContent;
    private Animation mRotate;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search,null);
        ButterKnife.bind(this,view);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    public void init(){
        mList=new ArrayList<>();
        mRotate= AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
        mRotate.setInterpolator(new LinearInterpolator());
        mProgress.startAnimation(mRotate);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter=new SearchResultAdapter(getActivity());
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
                showData();
                mRecyclerView.loadMoreComplete();
            }
        });
        mRecyclerView.setPullRefreshEnabled(false);
        showData();
    }
    public void showData(){
        if (!checkNetWork())
            return;
        Observable<GankBean> observable;
        if (mSearchContent==null){
            observable= HttpUtil.getGank(mPage+"");
            mPage++;
        }
        else {
            mPage++;
            observable = HttpUtil.search(Type.ALL, mSearchContent, mPage + "");
        }
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GankBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GankBean gankBean) {
                        mProgress.clearAnimation();
                        mLayout.setVisibility(View.GONE);
                        mAdapter.add(gankBean.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {}
                });
        mAdapter.notifyDataSetChanged();
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
        super.onDestroy();
    }

    @OnClick(R.id.fab)
    public void fab(View view){
        loadSearchDialog();
    }

    public String loadSearchDialog() {
        ArrayList<String> countryStored = SharedPreference.loadList(getActivity(), Utils.PREFS_NAME, Utils.KEY_COUNTRIES);
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
        LinearLayout parentToolbarSearch = (LinearLayout) view.findViewById(R.id.parent_toolbar_search);
        ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
        ImageView imgSearch = (ImageView) view.findViewById(R.id.img_tool_search);
        final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
        final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);
        Utils.setListViewHeightBasedOnChildren(listSearch);
        edtToolSearch.setHint("搜索你感兴趣的内容");
        final Dialog toolbarSearchDialog = new Dialog(getActivity(), R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(false);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();
        toolbarSearchDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK==keyCode)
                    toolbarSearchDialog.dismiss();
                else if (KeyEvent.KEYCODE_ENTER==keyCode){
                    toolbarSearchDialog.dismiss();
                    loadData(edtToolSearch.getText().toString());
                }
                return false;
            }
        });
        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        countryStored = (countryStored != null && countryStored.size() > 0) ? countryStored : new ArrayList<String>();
        final SearchAdapter searchAdapter = new SearchAdapter(getActivity(), countryStored, false);
        listSearch.setVisibility(View.VISIBLE);
        listSearch.setAdapter(searchAdapter);
        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String content = String.valueOf(adapterView.getItemAtPosition(position));
                SharedPreference.addList(getActivity(), Utils.PREFS_NAME, Utils.KEY_COUNTRIES, content);
                edtToolSearch.setText(content);
                toolbarSearchDialog.dismiss();
                loadData(edtToolSearch.getText().toString());
                listSearch.setVisibility(View.GONE);
            }
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                listSearch.setVisibility(View.VISIBLE);
                searchAdapter.updateList(mList, true);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    HttpUtil.search(Type.ALL,s.toString().trim(),"1")
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<GankBean>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(GankBean gankBean) {
                                    List<GankBean.ResultsBean> results = gankBean.getResults();

                                    if (results.size()>0){
                                        ArrayList<String> content=new ArrayList<>();
                                        for (GankBean.ResultsBean bean:results) {
                                            content.add(bean.getDesc());
                                        };
                                        listSearch.setVisibility(View.VISIBLE);
                                        searchAdapter.updateList(content, true);
                                    }
                                    else{
                                        listSearch.setVisibility(View.GONE);
                                        txtEmpty.setVisibility(View.VISIBLE);
                                        txtEmpty.setText("没有找到数据");
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onComplete() {}
                            });
                } else {
                    listSearch.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
                loadData(edtToolSearch.getText().toString());
            }
        });
        return edtToolSearch.getText().toString();
    }
    public void loadData(String searchContent){
        if (!checkNetWork())
            return;
        mLayout.setVisibility(View.VISIBLE);
        mProgress.startAnimation(mRotate);
        mPage=1;
        mSearchContent=searchContent;
        HttpUtil.search(Type.ALL,searchContent,mPage+"")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GankBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GankBean gankBean) {
                        mProgress.clearAnimation();
                        mLayout.setVisibility(View.GONE);
                        if (gankBean.getResults().size()>0)
                            mAdapter.setData(gankBean.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {}
                });
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
                    showData();
                }
            });
            return false;
        }
        return true;
    }

}
