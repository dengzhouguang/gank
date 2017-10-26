package com.dzg.gank.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by dengzhouguang on 2017/10/24.
 */

public interface BaiQiuService {
    @GET("text/page/{page}")
    Observable<ResponseBody> getData(@Path("page") String page);
}
