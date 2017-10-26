package com.dzg.gank.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.dzg.gank.App;
import com.dzg.gank.ItemDecoration.DividerGridItemDecoration;
import com.dzg.gank.R;
import com.dzg.gank.adapter.MovieAdapter;
import com.dzg.gank.listener.OnItemClickListener;
import com.dzg.gank.module.DianYingBean;
import com.dzg.gank.ui.activity.DYDetailActivity;
import com.dzg.gank.util.ACache;
import com.dzg.gank.util.CheckNetwork;
import com.dzg.gank.util.ListUtil;
import com.dzg.gank.util.ToastUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
public class MovieFragmen extends Fragment {
    private boolean isFirst=true;
    private boolean isLoading=false;
    private MovieAdapter adapter;
    private ArrayList<String> imgList=new ArrayList<>();
    private Animation rotate;
    private int page=0;
    private static String BASE_URL="http://www.ygdy8.net/html/gndy/dyzz/list_23_" ;
    private static String Host="http://www.ygdy8.net";
    private ACache cache=ACache.get(App.getInstance());
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
        View view=inflater.inflate(R.layout.dianying,container,false);
        ButterKnife.bind(this,view);
        init();
        if (checkNetWork())
        getDianYingByRxJava(++page);
        return view;
    }
    public boolean checkNetWork(){
        if (!CheckNetwork.isNetworkConnected(getActivity())){
            mProgressIv.setVisibility(View.GONE);
            mProgressIv.clearAnimation();
            ToastUtil.showToast("当前网络不可用，将加载上次使用的数据！");
            ArrayList<List<DianYingBean>>list= (ArrayList<List<DianYingBean>>) cache.getAsObject("dianyinglist");
            if (list!=null) {
                adapter.addAll(list.get(0));
                adapter.notifyDataSetChanged();
                mLlwaitionLl.setVisibility(View.GONE);
            }else {
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
                    getDianYingByRxJava(++page);
                }
            });}
            return false;
        }
        return true;
    }
    private void init() {
        adapter = new MovieAdapter(getActivity());
        rotate= AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
        rotate.setInterpolator(new LinearInterpolator());
        mProgressIv.startAnimation(rotate);
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
                getDianYingByRxJava(++page);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener<DianYingBean>() {
            @Override
            public void onClick(DianYingBean subjectsBean, int position) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("bean",subjectsBean);
                Intent intent=new Intent(getActivity(), DYDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(adapter);
    }
    public boolean getDianYingByRxJava(final int page) {
        if (isLoading) return false;
        isLoading=true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc=null;
                try {
                    doc=Jsoup.connect(BASE_URL+page+".html").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (doc!=null) {
                    Elements elements = doc.body().select("a.ulink");
                    List<DianYingBean> list=new ArrayList<>();
                    for (Element element:elements){
                        String title=element.text();
                        String url=element.attr("href");
                        try {
                            Document dyDoc=Jsoup.connect(Host+url).get();
                            Elements div=dyDoc.select("div.co_content8");
                            Elements img=div.select("img[src]");
                            String imgURL=img.get(0).attr("src");
                            String sumURL=img.get(1).attr("src");
                            String downURL=div.select("td").first().text();
                            Elements content=div.select("span");
                            content.select("center").remove();
                            content.select("font").remove();
                            content.select("a[href]").remove();
                            String text=content.text();

                            DianYingBean bean=new DianYingBean();
                            bean.setUrl(imgURL);
                            bean.setDownUrl(downURL);

                            int end=-1;
                            int pre=0;
                            if ((pre=text.indexOf("◎译　　名"))>0)
                                bean.setTranslation(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎片　　名"))>0)
                                bean.setName(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎国　　家"))>0)
                                bean.setCountry(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎类　　别"))>0)
                                bean.setType(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎语　　言"))>0)
                                bean.setLanguage(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎字　　幕"))>0)
                                bean.setSubtitle(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎IMDb评分"))>0)
                                bean.setScore(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎文件格式"))>0)
                                bean.setFormat(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎视频尺寸"))>0)
                                bean.setMeasure(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎文件大小"))>0)
                                bean.setSize(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎片　　长"))>0)
                                bean.setTime(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎导　　演"))>0)
                                bean.setDirector(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));
                            if ((pre=text.indexOf("◎简　　介"))>0)
                                bean.setStory(text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length()).replace("　　","").replace("◎",""));


                            if (text.indexOf("]")>0)
                            bean.setTitle(text.substring(0,text.indexOf("]")));
                            else if (bean.getTranslation()!=null)
                                bean.setTitle(bean.getTranslation().replace("◎译名　",""));
                            else if (bean.getName()!=null)
                                bean.setTitle(bean.getName().replace("◎片名　",""));
                            String actor=null;
//                            bean.setTitle(bean.getName().replace());
                            if ((pre=text.indexOf("◎主　　演"))>0)
                            { actor=text.substring(pre,(end=text.indexOf("◎",pre+1))>0?end:text.length());
                            actor=actor.replaceAll(" 　　　　　　","\r\n");
                                bean.setActors(actor.replace("◎主　　演　","主演\r\n"));
                            }
                            list.add(bean);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayList<List<DianYingBean>> lists=new ArrayList<List<DianYingBean>>();
                    lists.add(list);
                    adapter.addAll(list);
                    List<DianYingBean> data=adapter.getList();
                    data= ListUtil.removeDuplicate(data);
                    lists.add(data);
                    cache.put("dianyinglist",lists,259200);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            rotate.cancel();
                            mLlwaitionLl.setVisibility(View.GONE);
                            mRecyclerView.refreshComplete();
                            isLoading=false;
                        }
                    });

                }
            }
        }).start();
        return true;
    }
}
