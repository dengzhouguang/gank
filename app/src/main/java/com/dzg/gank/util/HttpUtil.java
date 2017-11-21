package com.dzg.gank.util;

import android.support.annotation.NonNull;

import com.dzg.gank.App;
import com.dzg.gank.repository.interfaces.BaiQiuService;
import com.dzg.gank.repository.interfaces.FuLiService;
import com.dzg.gank.repository.interfaces.GankSevice;
import com.dzg.gank.repository.interfaces.IMobileNewsApi;
import com.dzg.gank.repository.interfaces.IVideoApi;
import com.dzg.gank.repository.interfaces.MovieService;
import com.dzg.gank.mvp.model.Constants;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dengzhouguang on 2017/10/11.
 */

public class HttpUtil {
    private static GankSevice mGankSevice;
    private static HttpUtil instance;
    private static MovieService mMovieService;
    private static BaiQiuService mBaiQiuService;
    private static FuLiService mFuliService;
    private static IMobileNewsApi mIMobileNewsService;
    private static IVideoApi mIVideoService;

    public static void newInstance() {
        synchronized (Object) {
            if (instance == null) {
                synchronized (HttpUtil.class) {
                    if (instance == null)
                        instance = new HttpUtil();
                    OkHttpClient client = new OkHttpClient.Builder()
                            .cache(new Cache(FileUtil.getHttpCacheDir(App.getInstance()), Constants.HTTP_CACHE_SIZE))
                            .connectTimeout(Constants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                            .readTimeout(Constants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                            .build();
                    mGankSevice = new Retrofit.Builder()
                            .client(client)
                            .baseUrl("http://gank.io/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build().create(GankSevice.class);

                    mMovieService = new Retrofit.Builder()
                            .client(client)
                            .baseUrl("https://api.douban.com/")
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(MovieService.class);

//                http://www.qiushibaike.com/
                    mBaiQiuService = new Retrofit.Builder()
                            .client(client)
                            .baseUrl("http://www.qiushibaike.com/")
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build().create(BaiQiuService.class);
                    mFuliService = new Retrofit.Builder()
                            .client(client)
                            .baseUrl("http://gank.io/api/")
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(FuLiService.class);
                    // 指定缓存路径,缓存大小 50Mb
                    Cache cache = new Cache(new File(App.getInstance().getCacheDir(), "HttpCache"),
                            1024 * 1024 * 50);

                    // Cookie 持久化
                    ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.getInstance()));

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .cookieJar(cookieJar)
                            .cache(cache)
                            .addInterceptor(cacheControlInterceptor)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true);
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    builder.addInterceptor(interceptor);

                    mIMobileNewsService = new Retrofit.Builder()
                            .baseUrl("http://toutiao.com/")
                            .client(builder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build()
                            .create(IMobileNewsApi.class);
                    mIVideoService = new Retrofit.Builder()
                            .baseUrl("http://toutiao.com/")
                            .client(builder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build()
                            .create(IVideoApi.class);

                }
            }
        }
    }

    public static MovieService getDouBanService() {
        return mMovieService;
    }

    public static BaiQiuService getBaiQiuService() {
        return mBaiQiuService;
    }

    public static GankSevice getGankSevice(){
        return mGankSevice;
    }

    public static FuLiService getFuliService() {
        return mFuliService;
    }

    @NonNull
    public static IMobileNewsApi getShortVideoService() {
        return mIMobileNewsService;
    }

    public static IVideoApi getIVideoService() {
        return mIVideoService;
    }

    private static final Object Object = new Object();
    /**
     * 缓存机制
     * 在响应请求之后在 data/data/<包名>/cache 下建立一个response 文件夹，保持缓存数据。
     * 这样我们就可以在请求的时候，如果判断到没有网络，自动读取缓存的数据。
     * 同样这也可以实现，在我们没有网络的情况下，重新打开App可以浏览的之前显示过的内容。
     * 也就是：判断网络，有网络，则从网络获取，并保存到缓存中，无网络，则从缓存中获取。
     * https://werb.github.io/2016/07/29/%E4%BD%BF%E7%94%A8Retrofit2+OkHttp3%E5%AE%9E%E7%8E%B0%E7%BC%93%E5%AD%98%E5%A4%84%E7%90%86/
     */
    private static final Interceptor cacheControlInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetWorkUtil.isNetworkConnected(App.getInstance())) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }

            Response originalResponse = chain.proceed(request);
            if (NetWorkUtil.isNetworkConnected(App.getInstance())) {
                // 有网络时 设置缓存为默认值
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();
            } else {
                // 无网络时 设置超时为1周
                int maxStale = 60 * 60 * 24 * 7;
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };


}
