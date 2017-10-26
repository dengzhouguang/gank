package com.dzg.gank.util;

import com.dzg.gank.App;
import com.dzg.gank.api.BaiQiuService;
import com.dzg.gank.api.FuLiService;
import com.dzg.gank.api.GankApi;
import com.dzg.gank.api.MovieService;
import com.dzg.gank.module.Constants;
import com.dzg.gank.module.GankBean;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dengzhouguang on 2017/10/11.
 */

public  class HttpUtil {
    private static GankApi mService;
    private static HttpUtil instance;
    private static MovieService mMovieService;
    private static BaiQiuService mBaiQiuService;
    private static FuLiService mFuliService;
    public static void newInstance() {
        if (instance==null)
            synchronized (HttpUtil.class){
                if (instance==null)
                instance = new HttpUtil();
                OkHttpClient client = new OkHttpClient.Builder()
                        .cache(new Cache(FileUtil.getHttpCacheDir(App.getInstance()), Constants.HTTP_CACHE_SIZE))
                        .connectTimeout(Constants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                        .readTimeout(Constants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                        .build();
                mService =new Retrofit.Builder()
                        .client(client)
                        .baseUrl("http://gank.io/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build() .create(GankApi.class);

                mMovieService=new Retrofit.Builder()
                        .client(client)
                        .baseUrl("https://api.douban.com/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(MovieService.class);

//                http://www.qiushibaike.com/
                mBaiQiuService=new Retrofit.Builder()
                        .client(client)
                        .baseUrl("http://www.qiushibaike.com/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build().create(BaiQiuService.class);
                mFuliService=new Retrofit.Builder()
                        .client(client)
                        .baseUrl("http://gank.io/api/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(FuLiService.class);
            }
    }
    public static Observable<GankBean> getGank(String page){
    return mService.getGankByPage(page);
    }

    public static Observable<GankBean> search(String type,String searchContent, String page){
        return  mService.search(type,page,searchContent);
    }

    public static Observable<GankBean> getGankByDay(String year,String month,String day){
        return mService.getGankByDay(year, month, day);
    }

    public static MovieService getDouBanService(){
        return mMovieService;
    }

    public static BaiQiuService getBaiQiuService(){ return mBaiQiuService;};

    public static FuLiService getFuliService(){return mFuliService;}
}
